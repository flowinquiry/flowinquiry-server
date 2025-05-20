#!/bin/bash

helm uninstall flowinquiry
kubectl delete secret flowinquiry-backend-secret flowinquiry-frontend-secret
kubectl delete pvc --all