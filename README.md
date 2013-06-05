##Simple Network Client
Simple Network Client or SNC, is a set of tools for handling connections, and parsing data.

Current implementations
* [Sony Entertainment Network](https://github.com/KrobothSoftware/SimpleNetworkClient/wiki/Sony-Entertainment-Network)
 * [PlayStation Network](https://github.com/KrobothSoftware/SimpleNetworkClient/wiki/PlayStation-Network)
     * [US PlayStationNetwork](https://github.com/KrobothSoftware/SimpleNetworkClient/wiki/US-PlayStation-Network)

###Usage
**Be sure to read client's usage content.** All clients need `snc.jar` in build path, or appropriate `SNC` sources.

**- Dependencies**

* [SLF4J](http://www.slf4j.org/) - Logging library `slf4j-api.jar`

Don't use both jar files if one has the `android` postfix.

With ANT, calling the *Deploy* target will produce the jars.

Make sure to have `android.jar` in _dependencies_ folder. You can find that in `%ANDROID_SDK%/platforms/android-xx/android.jar`

***
* [Client](#client)
* [Networking](#networking)
* [Parsing](#parsing)
* [More Info](#more-info)


######Client
[Package](http://krobothsoftware.github.io/SimpleNetworkClient/javadoc/com/krobothsoftware/snc/package-summary.html) `com.krobothsoftware.snc`

Every SNC Implementation has a _Client_ based off [NetworkClient](http://krobothsoftware.github.io/SimpleNetworkClient/javadoc/com/krobothsoftware/snc/NetworkClient.html). Examples: `SonyEntertainmentNetwork.class` and `PlayStationNetwork.class`. Here you have access to `NetworkHelper`, and `Parser`.
***

######Networking
[Package](http://krobothsoftware.github.io/SimpleNetworkClient/javadoc/com/krobothsoftware/commons/network/package-summary.html) `com.krobothsoftware.commons.network` 

Each client has a `NetworkHelper` instance that is accessible by `NetworkClient.getNetworkHelper()`. Default properties for client's connections are set here.

`NetworkHelper.reset()` will reset all default values, but doesn't affect Cookies, Authentications, and Connection Listener.

**- Headers**

Default HTTP [headers](http://en.wikipedia.org/wiki/List_of_HTTP_header_fields). `NetworkHelper.setHeader(String, String)`. If value is _null_, it will remove header. If the client sets the same header in a connection, the default one will be ignored.
```java
// sets header "User-Agent"
networkHelper.setHeader("User-Agent", "Engine/1.0");
// removes header "User-Agent"
networkHelper.setHeader("User-Agent", null);
```
Headers set by default

| Name            | Value
|-----------------|:--------------------|
| User-Agent      | [NetworkHelper.AGENT_DEFAULT](http://krobothsoftware.github.io/SimpleNetworkClient/javadoc/com/krobothsoftware/commons/network/NetworkHelper.html#AGENT_DEFAULT)
| Accept          | text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
| Accept-Encoding | gzip, deflate
| Accept-Charset  | UTF-8

**- Timeouts, redirects, and proxies**

Just like default headers, they will be ignored if client connection sets them.

`NetworkHelper.setConnectTimeout(int)` - [URLConnection.setConnectTimeout](http://docs.oracle.com/javase/7/docs/api/java/net/URLConnection.html#setConnectTimeout(int\)). Default is 0.

`NetworkHelper.setReadTimeout(int)` - [URLConnection.setReadTimeout](http://docs.oracle.com/javase/7/docs/api/java/net/URLConnection.html#setReadTimeout(int\)). Default is 0.

`NetworkHelper.setMaxRedirects(int)` - Redirects are handled internally. Default is 20.

`NetworkHelper.setProxy(Proxy)` - Default is [Proxy.NO_PROXY](http://docs.oracle.com/javase/1.5.0/docs/api/java/net/Proxy.html#NO_PROXY). Don't use _null_.

**- Cookies**

_NetworkHelper_ has a [CookieManager](http://krobothsoftware.github.io/SimpleNetworkClient/javadoc/com/krobothsoftware/commons/network/CookieManager.html) that is retrieved by `NetworkHelper.getCookieManager()`. A new manager can be set `NetworkHelper.setCookieManager(CookieManager)`. Cookies in manager are set for connections, **but** may not update after connection has sent if request has an alternative cookie container. [Tokens](http://krobothsoftware.github.io/SimpleNetworkClient/javadoc/com/krobothsoftware/snc/Token.html) are an example. 
```java
CookieManager cookieManager = networkHelper.getCookieManager();
Cookie cookie = new Cookie(".domain.com", "name", "value");
		
// (true) overrides old cookie
cookieManager.putCookie(cookie, true);
```
All values can be set with [Builder](http://krobothsoftware.github.io/SimpleNetworkClient/javadoc/com/krobothsoftware/commons/network/value/Cookie.Builder.html).

**- Connection Listener**

[Listener](http://krobothsoftware.github.io/SimpleNetworkClient/javadoc/com/krobothsoftware/commons/network/ConnectionListener.html) for connections being set up and after connected. `NetworkHelper.setConnectionListener(ConnectionListener)`. Use [NetworkHelper.NULL\_CONNECTION\_LISTENER](http://krobothsoftware.github.io/SimpleNetworkClient/javadoc/com/krobothsoftware/commons/network/NetworkHelper.html#NULL_CONNECTION_LISTENER) instead of _null_.
```java
ConnectionListener connectionListener = new ConnectionListener() {
			
	@Override
	public void onRequest(HttpURLConnection connection, RequestBuilder builder) {
		// handle request before sent
				
	}
			
	@Override
	public void onFinish(HttpURLConnection connection) {
		// called after connection was sent
				
	}
	
};
		
networkHelper.setConnectionListener(connectionListener);
```
***
######Parsing
[Package](http://krobothsoftware.github.io/SimpleNetworkClient/javadoc/com/krobothsoftware/commons/parse/package-summary.html) `com.krobothsoftware.commons.parse`

[Parser](http://krobothsoftware.github.io/SimpleNetworkClient/javadoc/com/krobothsoftware/commons/parse/Parser.html) is accessible by `NetworkClient.getParser()`. Any _parser_ that implements [ParserInitializable](http://krobothsoftware.github.io/SimpleNetworkClient/javadoc/com/krobothsoftware/commons/parse/ParserInitializable.html) may create the parsing components by calling `ParserInitializable.init()`. This will try to initiate them and ignore any problems. Normally, they are initialized when needed.

`Parser` components may be retrieved by `Parser.getXmlParser()` and `Parser.getHtmlParser()`. 
***

######More Info
* [Change Log](https://github.com/KrobothSoftware/SimpleNetworkClient/wiki/Change-Log)
* [Known Issues](https://github.com/KrobothSoftware/SimpleNetworkClient/wiki/Known-Issues)
* [Implementing Client](https://github.com/KrobothSoftware/SimpleNetworkClient/wiki/Implementing-Network-Client)
* [Extensions](https://github.com/KrobothSoftware/SimpleNetworkClient/wiki/Extensions)
* [Commons](https://github.com/KrobothSoftware/SimpleNetworkClient/wiki/Commons)

Copyright © 2013 [Kyle Kroboth](https://github.com/KrobothSoftware). Distributed under the [Apache License Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
