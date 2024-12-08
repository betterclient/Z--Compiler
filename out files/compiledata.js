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

// Get classpath.map from gist
downloadAndStoreString('https://gist.githubusercontent.com/betterclient/780216963856da56ae7214e0de89bd22/raw/bd1be5a509f7b461d870ef3147fb9813b45a10ea/classpath.map%2520as%2520base64encoded%2520zip.txt', 'data');