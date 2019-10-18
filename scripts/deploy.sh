#!/bin/bash

gcloud builds submit --tag gcr.io/tibiawikiapi-246008/tibiawikiapi --project tibiawikiapi-246008
gcloud beta run deploy tibiawikiapi --image gcr.io/tibiawikiapi-246008/tibiawikiapi --platform managed --region europe-west1 --memory 1Gi --project tibiawikiapi-246008
