package me.potic.logger.controller

import com.google.common.base.Ticker
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import groovy.util.logging.Slf4j
import me.potic.logger.domain.LogRequest
import me.potic.logger.domain.User
import me.potic.logger.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import javax.annotation.PostConstruct
import java.security.Principal

import static me.potic.logger.util.Utils.maskForLog

@RestController
@Slf4j
class LogController {

    LoadingCache<String, Logger> cachedLoggers

    @Autowired
    UserService userService

    @PostConstruct
    void initCachedLoggers() {
        cachedLoggers(Ticker.systemTicker())
    }

    LoadingCache<String, Logger> cachedLoggers(Ticker ticker) {
        cachedLoggers = CacheBuilder.newBuilder()
                .ticker(ticker)
                .build(
                        new CacheLoader<String, Logger>() {

                            @Override
                            Logger load(String logger) {
                                LoggerFactory.getLogger(logger)
                            }
                        }
                )
    }

    @CrossOrigin
    @PostMapping(path = '/')
    @ResponseBody ResponseEntity log(final Principal principal, @RequestBody LogRequest logRequest) {
        try {
            MDC.put('service', logRequest.service)
            MDC.put('env', logRequest.env)

            User user = userService.findUserByAuth0Token(principal.token)
            MDC.put('user.id', user.id)

            cachedLoggers.get(logRequest.logger).info(logRequest.message)

            return new ResponseEntity(HttpStatus.OK)
        } catch (e) {
            MDC.clear()
            MDC.put('service', 'potic-logger')
            MDC.put('env', "${System.getenv('ENVIRONMENT_NAME') ?: 'dev'}")

            log.error "POST request for / with token=${maskForLog(principal.token)} and body=${logRequest} failed: $e.message", e

            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        } finally {
            MDC.clear()
        }
    }
}
