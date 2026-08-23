import { mkdirSync, writeFileSync } from "node:fs";
import {
  BASE_URL,
  fetchSnapshot,
  GOLDENS_DIR,
  goldenPath,
  loadEndpoints,
  prettyPrint,
} from "./lib.ts";

async function main(): Promise<void> {
  mkdirSync(GOLDENS_DIR, { recursive: true });
  const endpoints = loadEndpoints();
  console.log(`Capturing ${endpoints.length} endpoints from ${BASE_URL}`);

  let failed = 0;
  for (const endpoint of endpoints) {
    try {
      const snapshot = await fetchSnapshot(endpoint.path);
      writeFileSync(goldenPath(endpoint.id), prettyPrint(snapshot));
      console.log(
        `  ${endpoint.id}  ${snapshot.status}  ${endpoint.path}  ->  goldens/${endpoint.id}.json`,
      );
    } catch (error) {
      failed += 1;
      const message = error instanceof Error ? error.message : String(error);
      console.error(`  ${endpoint.id}  FAIL  ${endpoint.path}  ${message}`);
    }
  }

  if (failed > 0) {
    console.error(`\nCapture finished with ${failed} error(s).`);
    process.exit(1);
  }

  console.log("\nWrote goldens. Commit them if this refresh is intentional.");
}

await main();
