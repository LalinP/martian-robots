package martianrobots

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class KotestConfigurationTest : StringSpec({
    "Kotest assertions run" {
        2 + 2 shouldBe 4
    }
})
