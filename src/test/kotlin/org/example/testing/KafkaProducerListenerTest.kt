package org.example.testing

import io.micronaut.configuration.kafka.config.AbstractKafkaConfiguration
import io.micronaut.configuration.kafka.embedded.KafkaEmbedded
import io.micronaut.context.ApplicationContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.util.*

class KafkaProducerListenerTest {

    var config: Map<String, Any> = Collections.unmodifiableMap(object : HashMap<String, Any>() {
        init {
            put(AbstractKafkaConfiguration.EMBEDDED, true)
//            put(AbstractKafkaConfiguration.EMBEDDED_TOPICS, "test_topic")
        }
    })
    var kafkaEmbedded: KafkaEmbedded? = null

    @Test
    fun testListener() {

        ApplicationContext.run(config).use { ctx ->
            val holder = ctx.getBean(Holder::class.java)

            println("Kotlin Kafka up? " + if (pingHost("localhost", 9092, 1000)) "Yes Kafka is up" else "No!")

            val listener = ctx.getBean(TestListener::class.java)

            val producer = ctx.getBean(TestProducer::class.java)
            producer.send("key", "The Kotlin value!!")

            try {
                Thread.sleep(8000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            assertEquals("The Kotlin value!!", holder.message)
        }
    }

    companion object {

        fun pingHost(host: String, port: Int, timeout: Int): Boolean {
            try {
                Socket().use { socket ->
                    socket.connect(InetSocketAddress(host, port), timeout)
                    return true
                }
            } catch (e: IOException) {
                return false // Either timeout or unreachable or failed DNS lookup.
            }
        }
    }
}