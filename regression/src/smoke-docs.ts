import {
  BASE_URL,
  FETCH_RETRIES,
  sleep,
} from "./lib.ts";

const OPENAPI_30 = /^3\.0\.\d+$/;
const PETSTORE = /petstore\.swagger\.io/i;
// Must match WikiCategory.path / WikiCategory.tag in
// src/main/kotlin/com/tibiawiki/adapters/rest/WikiCategory.kt
const WIKI_CATEGORIES: ReadonlyArray<{ path: string; tag: string }> = [
  { path: "achievements", tag: "Achievements" },
  { path: "books", tag: "Books" },
  { path: "buildings", tag: "Buildings" },
  { path: "charms", tag: "Charms" },
  { path: "corpses", tag: "Corpses" },
  { path: "creatures", tag: "Creatures" },
  { path: "effects", tag: "Effects" },
  { path: "familiars", tag: "Familiars" },
  { path: "imbuements", tag: "Imbuements" },
  { path: "items", tag: "Items" },
  { path: "keys", tag: "Keys" },
  { path: "locations", tag: "Locations" },
  { path: "missiles", tag: "Missiles" },
  { path: "mounts", tag: "Mounts" },
  { path: "npcs", tag: "NPCs" },
  { path: "objects", tag: "Objects" },
  { path: "outfits", tag: "Outfits" },
  { path: "quests", tag: "Quests" },
  { path: "spells", tag: "Spells" },
  { path: "streets", tag: "Streets" },
  { path: "updates", tag: "Updates" },
  { path: "worlds", tag: "Worlds" },
];
const REQUIRED_INDEX_MARKERS = [
  'id="swagger-ui"',
  "swagger-ui-bundle.js",
  "swagger-initializer.js",
];

type RawResponse = {
  path: string;
  status: number;
  contentType: string;
  body: string;
};

let failed = 0;

function ok(label: string): void {
  console.log(`ok    ${label}`);
}

function fail(label: string, message: string): void {
  failed += 1;
  console.error(`FAIL  ${label}  ${message}`);
}

function contentTypeOf(response: RawResponse): string {
  return response.contentType.split(";")[0]?.trim().toLowerCase() ?? "";
}

function resolveAsset(ref: string): string {
  if (ref.startsWith("/")) {
    return ref;
  }
  return `/swagger-ui/${ref.replace(/^\.\//, "")}`;
}

function relativeRefs(html: string): string[] {
  const found = new Set<string>();
  const attr = /(?:href|src)=["']([^"']+)["']/gi;
  let match: RegExpExecArray | null;
  while ((match = attr.exec(html)) !== null) {
    const ref = match[1];
    if (
      ref.startsWith("http://") ||
      ref.startsWith("https://") ||
      ref.startsWith("//") ||
      ref.startsWith("data:") ||
      ref.startsWith("#")
    ) {
      continue;
    }
    found.add(ref);
  }
  return [...found];
}

function expectedTypeHint(path: string): string[] {
  if (path.endsWith(".html")) {
    return ["text/html"];
  }
  if (path.endsWith(".css")) {
    return ["text/css"];
  }
  if (path.endsWith(".js")) {
    return ["javascript", "ecmascript"];
  }
  if (path.endsWith(".png")) {
    return ["image/png", "image/"];
  }
  if (path.endsWith(".ico") || path.endsWith(".svg")) {
    return ["image/", "icon"];
  }
  return [];
}

function typeLooksRight(path: string, contentType: string): boolean {
  const hints = expectedTypeHint(path);
  if (hints.length === 0) {
    return contentType.length > 0;
  }
  return hints.some((hint) => contentType.includes(hint));
}

function isServiceDocsUrl(raw: unknown, baseUrl: string): boolean {
  if (typeof raw !== "string" || raw.trim().length === 0) {
    return false;
  }
  try {
    const resolved = new URL(raw, `${baseUrl}/`);
    const base = new URL(`${baseUrl}/`);
    if (resolved.origin !== base.origin) {
      return false;
    }
    return resolved.pathname === "/api-docs" || resolved.pathname === "/api-docs/";
  } catch {
    return false;
  }
}

async function fetchRaw(path: string): Promise<RawResponse> {
  const url = `${BASE_URL}${path}`;
  const attempts = Math.max(1, FETCH_RETRIES);
  let lastError: unknown;

  for (let attempt = 1; attempt <= attempts; attempt++) {
    try {
      const response = await fetch(url);
      const body = await response.text();
      const raw: RawResponse = {
        path,
        status: response.status,
        contentType: response.headers.get("content-type") ?? "",
        body,
      };
      if (response.status < 500 || attempt === attempts) {
        return raw;
      }
      console.error(`  retry ${attempt}/${attempts}  ${path}  HTTP ${response.status}`);
    } catch (error) {
      lastError = error;
      if (attempt === attempts) {
        throw error;
      }
      const message = error instanceof Error ? error.message : String(error);
      console.error(`  retry ${attempt}/${attempts}  ${path}  ${message}`);
    }
    await sleep(1000 * attempt);
  }

  throw lastError instanceof Error ? lastError : new Error(`Failed to fetch ${url}`);
}

async function get(path: string): Promise<RawResponse | null> {
  try {
    return await fetchRaw(path);
  } catch (error) {
    const message = error instanceof Error ? error.message : String(error);
    fail(path, message);
    return null;
  }
}

async function assertStatusAndBody(
  path: string,
  typeHints: string[],
): Promise<RawResponse | null> {
  const response = await get(path);
  if (!response) {
    return null;
  }
  if (response.status !== 200) {
    fail(path, `expected HTTP 200, got ${response.status}`);
    return null;
  }
  if (response.body.trim().length === 0) {
    fail(path, "empty body");
    return null;
  }
  const contentType = contentTypeOf(response);
  if (typeHints.length > 0 && !typeHints.some((hint) => contentType.includes(hint))) {
    fail(path, `unexpected content-type ${JSON.stringify(response.contentType)}`);
    return null;
  }
  return response;
}

async function assertHealth(path: string): Promise<void> {
  const response = await assertStatusAndBody(path, ["application/json", "json"]);
  if (!response) {
    return;
  }
  try {
    const json = JSON.parse(response.body) as { status?: unknown };
    if (json.status !== "UP") {
      fail(path, `expected status UP, got ${JSON.stringify(json.status)}`);
      return;
    }
    ok(`${path}  UP`);
  } catch (error) {
    const message = error instanceof Error ? error.message : String(error);
    fail(path, `not JSON: ${message}`);
  }
}

function operationTags(pathItem: unknown): string[] {
  if (pathItem === null || typeof pathItem !== "object") {
    return [];
  }
  const tags = new Set<string>();
  for (const method of ["get", "put", "post", "delete", "patch"]) {
    const operation = (pathItem as Record<string, unknown>)[method];
    if (operation === null || typeof operation !== "object") {
      continue;
    }
    const rawTags = (operation as { tags?: unknown }).tags;
    if (!Array.isArray(rawTags)) {
      continue;
    }
    for (const tag of rawTags) {
      if (typeof tag === "string") {
        tags.add(tag);
      }
    }
  }
  return [...tags];
}

function specTagNames(spec: { tags?: unknown }): string[] {
  if (!Array.isArray(spec.tags)) {
    return [];
  }
  return spec.tags
    .map((tag) =>
      tag !== null && typeof tag === "object" && typeof (tag as { name?: unknown }).name === "string"
        ? (tag as { name: string }).name
        : null,
    )
    .filter((name): name is string => name !== null);
}

function assertWikiCategoryDocs(
  docsPath: string,
  spec: { paths?: unknown; tags?: unknown },
): void {
  if (spec.paths === null || typeof spec.paths !== "object") {
    fail(docsPath, "paths must be an object to check WikiCategory expansions");
    return;
  }
  const paths = spec.paths as Record<string, unknown>;
  const generic = Object.keys(paths).filter((key) => key.includes("{category"));
  if (generic.length > 0) {
    fail(
      docsPath,
      `generic /api/{category} template still published (should be concrete WikiCategory paths): ${generic.join(", ")}`,
    );
  } else {
    ok(`${docsPath}  no generic /api/{category} template`);
  }

  const tags = specTagNames(spec);
  for (const category of WIKI_CATEGORIES) {
    const collection = `/api/${category.path}`;
    const byName = `/api/${category.path}/{name}`;
    if (!(collection in paths)) {
      fail(docsPath, `missing concrete path ${collection}`);
    } else {
      const collectionTags = operationTags(paths[collection]);
      if (!collectionTags.includes(category.tag)) {
        fail(docsPath, `${collection} should be tagged ${category.tag}, got ${JSON.stringify(collectionTags)}`);
      } else {
        ok(`${docsPath}  ${collection}  tag=${category.tag}`);
      }
    }
    if (!(byName in paths)) {
      fail(docsPath, `missing concrete path ${byName}`);
    } else {
      const byNameTags = operationTags(paths[byName]);
      if (!byNameTags.includes(category.tag)) {
        fail(docsPath, `${byName} should be tagged ${category.tag}, got ${JSON.stringify(byNameTags)}`);
      }
    }
    if (!tags.includes(category.tag)) {
      fail(docsPath, `tags must include ${category.tag}`);
    }
  }
}

const HUNTING_SLASH_NOTE = "Wiki titles may contain slashes";

function assertActuatorsStayOutOfSpec(
  docsPath: string,
  spec: { paths?: unknown },
): void {
  if (spec.paths === null || typeof spec.paths !== "object") {
    fail(docsPath, "paths must be an object to check actuator exclusion");
    return;
  }
  const actuatorPaths = Object.keys(spec.paths as Record<string, unknown>).filter((key) =>
    key.startsWith("/actuator"),
  );
  if (actuatorPaths.length > 0) {
    fail(
      docsPath,
      `actuators must stay out of the public spec (springdoc.paths-to-match=/api/**): ${actuatorPaths.join(", ")}`,
    );
  } else {
    ok(`${docsPath}  no /actuator paths`);
  }
}

function assertHuntingPlaceSlashNote(
  docsPath: string,
  spec: { paths?: unknown },
): void {
  if (spec.paths === null || typeof spec.paths !== "object") {
    fail(docsPath, "paths must be an object to check hunting-place slash note");
    return;
  }
  const paths = spec.paths as Record<string, unknown>;
  if (!("/api/huntingplaces" in paths)) {
    fail(docsPath, "missing /api/huntingplaces");
    return;
  }
  if ("/api/huntingplaces/{name}" in paths) {
    fail(
      docsPath,
      "must not invent /api/huntingplaces/{name}; runtime mapping is /** for slashy titles",
    );
  }
  const byNameKeys = Object.keys(paths).filter((key) => key.startsWith("/api/huntingplaces/"));
  if (byNameKeys.length === 0) {
    fail(docsPath, "missing hunting-place by-name path (expected /** mapping, not {name})");
    return;
  }
  const descriptions = byNameKeys.map((key) => {
    const pathItem = paths[key];
    if (pathItem === null || typeof pathItem !== "object") {
      return "";
    }
    const get = (pathItem as { get?: { description?: unknown } }).get;
    return typeof get?.description === "string" ? get.description : "";
  });
  if (!descriptions.some((description) => description.includes(HUNTING_SLASH_NOTE))) {
    fail(
      docsPath,
      `hunting-place by-name docs must note slashy titles; paths=${byNameKeys.join(", ")}`,
    );
  } else {
    ok(`${docsPath}  hunting-place /** notes slashy names`);
  }
}

async function main(): Promise<void> {
  console.log(`Docs / health smoke against ${BASE_URL}`);

  const index = await assertStatusAndBody("/swagger-ui/index.html", ["text/html"]);
  if (index) {
    const missing = REQUIRED_INDEX_MARKERS.filter((marker) => !index.body.includes(marker));
    if (missing.length > 0) {
      fail(index.path, `HTML missing ${missing.join(", ")}`);
    } else {
      ok(`${index.path}  swagger-ui shell`);
    }

    const assets = relativeRefs(index.body).map(resolveAsset);
    const requiredAssets = [
      "/swagger-ui/swagger-ui.css",
      "/swagger-ui/index.css",
      "/swagger-ui/swagger-ui-bundle.js",
      "/swagger-ui/swagger-ui-standalone-preset.js",
      "/swagger-ui/swagger-initializer.js",
    ];
    for (const required of requiredAssets) {
      if (!assets.includes(required)) {
        fail(index.path, `did not reference ${required}`);
      }
    }

    for (const asset of assets) {
      const response = await get(asset);
      if (!response) {
        continue;
      }
      if (response.status !== 200) {
        fail(asset, `expected HTTP 200, got ${response.status}`);
        continue;
      }
      if (response.body.length === 0) {
        fail(asset, "empty body");
        continue;
      }
      const contentType = contentTypeOf(response);
      if (!typeLooksRight(asset, contentType)) {
        fail(asset, `unexpected content-type ${JSON.stringify(response.contentType)}`);
        continue;
      }
      ok(`${asset}  ${response.status}  ${contentType || "(no content-type)"}`);
    }
  }

  const initializer = await assertStatusAndBody("/swagger-ui/swagger-initializer.js", [
    "javascript",
    "ecmascript",
  ]);
  if (initializer) {
    if (PETSTORE.test(initializer.body)) {
      fail(
        initializer.path,
        "contains petstore.swagger.io (stock Swagger UI default — UI will not show TibiaWikiApi)",
      );
    } else {
      ok(`${initializer.path}  no petstore URL`);
    }
  }

  const swaggerConfig = await assertStatusAndBody("/api-docs/swagger-config", [
    "application/json",
    "json",
  ]);
  let configUrl: unknown;
  if (swaggerConfig) {
    try {
      const json = JSON.parse(swaggerConfig.body) as { url?: unknown };
      configUrl = json.url;
      if (!isServiceDocsUrl(json.url, BASE_URL)) {
        fail(
          swaggerConfig.path,
          `url must be this service's /api-docs (same-origin), got ${JSON.stringify(json.url)}`,
        );
      } else {
        ok(`${swaggerConfig.path}  url=${JSON.stringify(json.url)}`);
      }
    } catch (error) {
      const message = error instanceof Error ? error.message : String(error);
      fail(swaggerConfig.path, `not JSON: ${message}`);
    }
  }

  if (initializer && !PETSTORE.test(initializer.body)) {
    const initializerPointsAtDocs =
      initializer.body.includes("/api-docs") || initializer.body.includes("swagger-config");
    if (!initializerPointsAtDocs && !isServiceDocsUrl(configUrl, BASE_URL)) {
      fail(
        initializer.path,
        "neither initializer nor swagger-config resolve OpenAPI to this service's /api-docs",
      );
    } else if (initializerPointsAtDocs) {
      ok(`${initializer.path}  resolves this API`);
    }
  }

  const apiDocs = await assertStatusAndBody("/api-docs", ["application/json", "json"]);
  if (apiDocs) {
    try {
      const spec = JSON.parse(apiDocs.body) as {
        openapi?: unknown;
        info?: { title?: unknown };
        paths?: unknown;
      };
      if (typeof spec.openapi !== "string" || !OPENAPI_30.test(spec.openapi)) {
        fail(
          apiDocs.path,
          `openapi must match ^3.0.\\d+$ (some Swagger UI builds reject 3.1.x ` +
            `with "The provided definition does not specify a valid version field"); ` +
            `got ${JSON.stringify(spec.openapi)}`,
        );
      } else {
        ok(`${apiDocs.path}  openapi=${spec.openapi}`);
      }
      if (spec.info?.title !== "TibiaWikiApi") {
        fail(apiDocs.path, `info.title must be TibiaWikiApi, got ${JSON.stringify(spec.info?.title)}`);
      } else {
        ok(`${apiDocs.path}  title=TibiaWikiApi`);
      }
      const pathCount =
        spec.paths !== null && typeof spec.paths === "object" ? Object.keys(spec.paths).length : 0;
      if (pathCount < 1) {
        fail(apiDocs.path, "paths must contain at least one entry");
      } else {
        ok(`${apiDocs.path}  ${pathCount} path(s)`);
      }
      assertWikiCategoryDocs(apiDocs.path, spec);
      assertActuatorsStayOutOfSpec(apiDocs.path, spec);
      assertHuntingPlaceSlashNote(apiDocs.path, spec);
    } catch (error) {
      const message = error instanceof Error ? error.message : String(error);
      fail(apiDocs.path, `not JSON: ${message}`);
    }
  }

  await assertHealth("/actuator/health");
  await assertHealth("/actuator/health/readiness");

  if (failed > 0) {
    console.error(
      `\n${failed} docs/health check(s) failed. This is infra/UI health, not wiki golden drift.`,
    );
    process.exit(1);
  }

  console.log("\nDocs and health smoke passed.");
}

await main();
