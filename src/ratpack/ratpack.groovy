import org.slf4j.LoggerFactory
import org.slf4j.MDC
import ratpack.http.Status

import static ratpack.groovy.Groovy.ratpack
import static ratpack.jackson.Jackson.jsonNode

String environmentName = System.getenv('ENVIRONMENT_NAME') ?: 'dev'

ratpack {
    serverConfig {
        port (environmentName == 'dev' ? 40410 : 5050)
    }

    handlers {
        post {
            parse(jsonNode()).then({ logRequest ->
                try {
                    MDC.put('service', logRequest.get('service')?.asText())
                    MDC.put('env', logRequest.get('env')?.asText())

                    String logger = logRequest.get('logger')?.asText()
                    String loglevel = logRequest.get('loglevel')?.asText()
                    String message = logRequest.get('message')?.asText()

                    LoggerFactory.getLogger(logger)."${loglevel.toLowerCase()}"(message)

                    response.status(Status.OK).send()
                } catch (e) {
                    LoggerFactory.getLogger('me.potic.logger').warn('Log request failed', e)

                    response.status(Status.of(500)).send("Log request failed because of ${e.class.name}: ${e.message}")
                } finally {
                    MDC.clear()
                }
            })
        }
    }
}
