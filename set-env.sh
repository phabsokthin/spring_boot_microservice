#!/bin/bash

export DB_PRODUCT_PASSWORD=admin123
export DB_ORDER_PASSWORD=admin123
export MONGODB_URI=mongodb://localhost:27017/dbclient

export JWT_SECRET="my-super-secret-key-for-jwt-authentication-2026"

# command for start config server 
# ====== command below for  =====

# source set-env.sh
# echo $JWT_SECRET // for check variable

# note when run command must be stand in project root folder
# source set-env.sh
# echo $JWT_SECRET // for check variable