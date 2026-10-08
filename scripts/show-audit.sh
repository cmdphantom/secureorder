#!/bin/bash
# Show audit logs from the database container
docker compose logs db | grep "AUDIT:"