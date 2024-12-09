async function downloadAndStoreString(url, key) {
    if(localStorage.getItem(key) !== null && localStorage.getItem(key) !== "") {
        console.log('Cached before!');
        return;
    }

    try {
        // Fetch the string from the URL
        const response = await fetch(url);
        const text = await response.text(); // Get the content as text

        // Save the string to localStorage
        localStorage.setItem(key, text);
        console.log('Done!');
    } catch (error) {
        console.error('Error: ', error);
    }
}

var a = 'https://gist.githubusercontent.com/betterclient/780216963856da56ae7214e0de89bd22/raw/e317dabe0ad7b48a68cb313e9b71225d38946131/claspath%2520shortened.map';

// Get classpath.map from gist
downloadAndStoreString(a, 'data0');