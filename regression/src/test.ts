import { existsSync } from "node:fs";
import {
  BASE_URL,
  describeDiff,
  fetchSnapshot,
  goldenPath,
  loadEndpoints,
  loadGolden,
  normalizeSnapshot,
  prettyPrint,
} from "./lib.ts";

async function main(): Promise<void> {
  const endpoints = loadEndpoints();
  console.log(`Comparing ${endpoints.length} endpoints against ${BASE_URL}`);

  let failed = 0;
  for (const endpoint of endpoints) {
    const file = goldenPath(endpoint.id);
    if (!existsSync(file)) {
      failed += 1;
      console.error(
        `FAIL  ${endpoint.id}  missing golden goldens/${endpoint.id}.json (run bun run capture)`,
      );
      continue;
    }

    try {
      const actual = prettyPrint(normalizeSnapshot(await fetchSnapshot(endpoint.path)));
      const expected = prettyPrint(normalizeSnapshot(loadGolden(endpoint.id)));

      if (actual === expected) {
        console.log(`ok    ${endpoint.id}  ${endpoint.path}`);
        continue;
      }

      failed += 1;
      console.error(`FAIL  ${endpoint.id}  ${endpoint.path}`);
      console.error(describeDiff(expected, actual));
    } catch (error) {
      failed += 1;
      const message = error instanceof Error ? error.message : String(error);
      console.error(`FAIL  ${endpoint.id}  ${endpoint.path}  ${message}`);
    }
  }

  if (failed > 0) {
    console.error(
      `\n${failed} endpoint(s) differed. Wiki data can drift; refresh goldens with bun run capture if the change is expected.`,
    );
    process.exit(1);
  }

  console.log("\nAll endpoints match their goldens.");
}

await main();
