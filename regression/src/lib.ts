import { readFileSync } from "node:fs";
import { join } from "node:path";

export type Endpoint = {
  id: string;
  path: string;
  description: string;
};

export type Snapshot = {
  path: string;
  status: number;
  body: unknown;
};

export const BASE_URL = (process.env.BASE_URL ?? "http://localhost:8080").replace(
  /\/$/,
  "",
);

export const ROOT_DIR = join(import.meta.dir, "..");
export const ENDPOINTS_PATH = join(ROOT_DIR, "endpoints.json");
export const GOLDENS_DIR = join(ROOT_DIR, "goldens");

export function loadEndpoints(): Endpoint[] {
  const parsed: unknown = JSON.parse(readFileSync(ENDPOINTS_PATH, "utf8"));
  if (!Array.isArray(parsed)) {
    throw new Error(`Expected ${ENDPOINTS_PATH} to contain an array of cases`);
  }
  return parsed as Endpoint[];
}

/** Recursively sort object keys so snapshots compare stably. */
export function normalizeJson(value: unknown): unknown {
  if (Array.isArray(value)) {
    return value.map(normalizeJson);
  }
  if (value !== null && typeof value === "object") {
    const obj = value as Record<string, unknown>;
    const sorted: Record<string, unknown> = {};
    for (const key of Object.keys(obj).sort()) {
      sorted[key] = normalizeJson(obj[key]);
    }
    return sorted;
  }
  return value;
}

export function prettyPrint(value: unknown): string {
  return `${JSON.stringify(value, null, 2)}\n`;
}

export function parseBody(text: string): unknown {
  const trimmed = text.trim();
  if (trimmed.length === 0) {
    return null;
  }
  try {
    return JSON.parse(trimmed);
  } catch {
    return text;
  }
}

export function goldenPath(id: string): string {
  return join(GOLDENS_DIR, `${id}.json`);
}

export function loadGolden(id: string): Snapshot {
  return JSON.parse(readFileSync(goldenPath(id), "utf8")) as Snapshot;
}

export function normalizeSnapshot(snapshot: Snapshot): Snapshot {
  return {
    path: snapshot.path,
    status: snapshot.status,
    body: normalizeJson(snapshot.body),
  };
}

export async function fetchSnapshot(path: string): Promise<Snapshot> {
  const url = `${BASE_URL}${path}`;
  const response = await fetch(url, {
    headers: { Accept: "application/json" },
  });
  const text = await response.text();
  return {
    path,
    status: response.status,
    body: normalizeJson(parseBody(text)),
  };
}

/** Isolated region around the first/last mismatch, capped for readability. */
export function describeDiff(expected: string, actual: string): string {
  if (expected === actual) {
    return "";
  }

  const e = expected.split("\n");
  const a = actual.split("\n");
  const min = Math.min(e.length, a.length);

  let first = 0;
  while (first < min && e[first] === a[first]) {
    first += 1;
  }

  let lastE = e.length - 1;
  let lastA = a.length - 1;
  while (lastE > first && lastA > first && e[lastE] === a[lastA]) {
    lastE -= 1;
    lastA -= 1;
  }

  const context = 2;
  const maxLines = 40;
  const fromE = Math.max(0, first - context);
  const toE = Math.min(e.length - 1, lastE + context);
  const fromA = Math.max(0, first - context);
  const toA = Math.min(a.length - 1, lastA + context);

  const goldenSlice = e.slice(fromE, toE + 1);
  const actualSlice = a.slice(fromA, toA + 1);
  const truncated =
    goldenSlice.length > maxLines || actualSlice.length > maxLines;

  const out = [
    `  first difference at line ${first + 1}`,
    `  --- golden (lines ${fromE + 1}-${toE + 1} of ${e.length})`,
    ...goldenSlice.slice(0, maxLines).map((line) => `  - ${line}`),
    `  +++ actual (lines ${fromA + 1}-${toA + 1} of ${a.length})`,
    ...actualSlice.slice(0, maxLines).map((line) => `  + ${line}`),
  ];
  if (truncated) {
    out.push("  ... (diff truncated)");
  }
  return out.join("\n");
}
