package me.kleinreveche.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.hsts.*
import io.ktor.server.plugins.httpsredirect.*

fun Application.configureHTTP() {
    install(HSTS) {
        includeSubDomains = true
    }
    install(HttpsRedirect) {
        // The port to redirect to. By default 443, the default HTTPS port.
        sslPort = 443
        // 301 Moved Permanently, or 302 Found redirect.
        permanentRedirect = true
    }
}
