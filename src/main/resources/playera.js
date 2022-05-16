var is_vst = true;
var m3u8Uri = "";
var locatUri = "";

function bdecode(data) {
    var keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
    var a1, a2, a3, h1, h2, h3, h4, bits, i = 0,
    ac = 0,
    dec = "",
    tmp_arr = [];
    if (!data) {
        return data;
    }
    data += '';
    do {
        h1 = keyStr.indexOf(data.charAt(i++));
        h2 = keyStr.indexOf(data.charAt(i++));
        h3 = keyStr.indexOf(data.charAt(i++));
        h4 = keyStr.indexOf(data.charAt(i++));
        bits = h1 << 18 | h2 << 12 | h3 << 6 | h4;
        a1 = bits >> 16 & 0xff;
        a2 = bits >> 8 & 0xff;
        a3 = bits & 0xff;
        if (h3 == 64) {
            tmp_arr[ac++] = String.fromCharCode(a1);
        } else if (h4 == 64) {
            tmp_arr[ac++] = String.fromCharCode(a1, a2);
        } else {
            tmp_arr[ac++] = String.fromCharCode(a1, a2, a3);
        }
    } while (i < data.length);
    dec = tmp_arr.join('');
    return dec;
}

function bdecodeb(str,key) {
    string = bdecode(str);
    len = key.length;
    code = "";
    for (i = 0; i < string.length; i++) {
      k = i % len;
      code += String.fromCharCode(string.charCodeAt(i) ^ key.charCodeAt(k));
    }
    stra = bdecode(code);
    return stra;
 }
 
function is_null(uri) {
	if ("undefined" != uri && "" != uri && uri != null && uri.length > 0) {
		return false;
	}
	return true;
}

function return_break() {
	if (is_vst) {
		if (locatUri.indexOf("?act=play") > 0) {
			vstPlayer.src = "";
		}
		vst.GoBack();
	} else {
		history.back();
	}
}

function setPlayerUri(uri) {
	m3u8Uri = uri;
	return m3u8Uri;
}

function full_player() {
	if (!is_null(m3u8Uri)) {
		if (is_vst) {
			vstPlayer.src = "";
			vst.open_player(m3u8Uri);
		} else {
			vstPlayer.src = m3u8Uri;
			vstPlayer.play();
		}
	}
}

function urlParsing(uri) {
	if (!is_null(uri) && uri.indexOf("://") > 0) {
		var vid = uri.substr(uri.indexOf("://") + 3);
		if (uri.indexOf("http://") == 0) {
			if (uri.indexOf("") > -1) {
			}
			return setPlayerUri(uri);
		} else if (uri.indexOf("://") > 2) {
			return setPlayerUri(uri);
		}
	}
}

function startPlayer(uri) {
	uri = uri.split("").reverse().join("");
  	uri = bdecodeb(uri,hken);
	uri = uri.replace("token=123", "token="+token);
	uri = uri.replace("token="+hkens, "token="+token);
	uri = uri.replace(hken, "");
	m3u8Uri = "";
	if (!is_null(uri)) {
		return urlParsing(uri);
	}
}