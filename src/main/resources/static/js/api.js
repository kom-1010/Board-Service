function call(url, method, request){
    const options = {
        method: method,
        headers: {"Content-Type" : "application/json"}
    };

    if(request)
        options.body = JSON.stringify(request);

    return fetch(url, options).then(response => {
            if(response.ok)
            return response.json();
        else
            throw response;
    });
}