

function maven_deploy_nextgen() {
 pushd "${NEXTGEN_HOME}/nextgen-pu/nextgen-pu-deploy" &> /dev/null
 mvn -Denv=local -Dtask=deploy install  &>/dev/null || {
     echo "Deploy failed!"
     exit 1
 }
 popd >/dev/null
}

function maven_deploy_nordea() {
    maven_deploy_market_app_pu nordea
}

function maven_deploy_market_pu() {
    maven_deploy_market_app_pu omx
}

function maven_deploy_market_app_pu() {
    local app = $1
    pushd "${NEXTGEN_HOME}/nextgen-market/nextgen-market-pu/nextgen-market-pu-deploy" &> /dev/null
    mvn -Denv=local -Dtask=deploy-${app} install >/dev/null || {
        echo "Deploy failed!"
        exit 1
    }
    popd > /dev/null
}

function maven_build() {
    if [[ -n "$1" && "$1" == "verbose" ]]; then
        mvn clean install -T 1C || {
            echo "Build failed!"
            exit 1;
        }
    else
        mvn clean install -T 1C 2>&1 >/dev/null || {
            echo "Build failed!"
            exit 1;
        }
    fi
}

function maven_build_nextgen() {
    pushd "${NEXTGEN_HOME}" > /dev/null
    maven_build $1
    popd > /dev/null
}

function maven_build_nextgen_pu() {
    pushd "${NEXTGEN_HOME}/nextgen-pu" &> /dev/null
    echo -n "Building nextgen_pu ..."
    maven_build $1
    echo " success!"
    popd > /dev/null
}


function maven_build_market_pu() {                 
    pushd "${NEXTGEN_HOME}/nextgen-market/nextgen-market-pu" > /dev/null
    maven_build $1
    popd >/dev/null
}

function clean_deploy_dir() {
   pushd "${GIGASPACES_HOME}" > /dev/null
   rm -rf deploy/*
   rm -rf work/processing-units/*
   rm -rf logs/*
   index_clean
   popd > /dev/null
}

function index_clean() {
    curl -s -XDELETE http://localhost:9200/_all >/dev/null
}

function undeploy_nextgen() {
    LOOKUPGROUPS=nextgen ${gigaspace_home}/bin/gs.sh undeploy-application nextgen-application
}

function undeploy_market_omx() {
    LOOKUPGROUPS=nextgen
    ${gigaspace_home}/bin/gs.sh undeploy-application nextgen-market-omx
}

function undeploy_market_nordea() {
    LOOKUPGROUPS=nextgen ${gigaspace_home}/bin/gs.sh undeploy-application nextgen-market-nordea
}
