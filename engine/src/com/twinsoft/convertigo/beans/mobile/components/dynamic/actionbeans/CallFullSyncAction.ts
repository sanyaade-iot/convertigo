    /**
     * Function CallFullSyncAction
     *   
     * @param page  , the current page
     * @param props , the object which holds properties key-value pairs
     * @param vars  , the object which holds variables key-value pairs
     */
    CallFullSyncAction(page: C8oPageBase, props, vars) : Promise<any> {
        return new Promise((resolve, reject) => {
            let r:string = props.requestable.substring(props.requestable.indexOf('.')+1);
            let v:string = props.verb;
            let m:string = props.marker;
            let rvm:string = r + '.' + v + (m != '' ? '#':'')+ m;
            page.call("fs://" + rvm,C8oCafUtils.merge({},vars),null,500)
            .then((res:any) => {resolve(res)}).catch((error:any) => {reject(error)})
        });
    }
    