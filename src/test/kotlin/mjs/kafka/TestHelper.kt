package mjs.kafka

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import java.util.UUID
import kotlin.random.Random
import kotlin.random.nextUInt

/**
 * Generate a random, valid Australian Company Number.
 *
 * See https://asic.gov.au/for-business/registering-a-company/steps-to-register-a-company/australian-company-numbers/australian-company-number-digit-check/
 * for the algorithm.
 */
fun randomAcn(): String {
    val baseDigits = listOf(
        Random.nextInt(10), Random.nextInt(10), Random.nextInt(10), Random.nextInt(10),
        Random.nextInt(10), Random.nextInt(10), Random.nextInt(10), Random.nextInt(10),
    )
    return acnFromBase(baseDigits)
}

private fun acnFromBase(baseDigits: List<Int>): String {
    val weighted = baseDigits.mapIndexed { idx, num -> (8 - idx) * num }
    val sumWeighted = weighted.sum()
    val rem = sumWeighted % 10
    val complement = (10 - rem) % 10
    return baseDigits.joinToString("") { it.toString() } + complement.toString()
}

class AcnFromBaseTest : FunSpec({
    context("ACN calculation") {
        withData(
            "000000019", "000250000", "000500005", "000750005",
            "001000004", "001250004", "001500009", "001749999",
            "001999999", "002249998", "002499998", "002749993",
            "002999993", "003249992", "003499992", "003749988",
            "003999988", "004249987", "004499987", "004749982",
            "004999982", "005249981", "005499981", "005749986",
            "005999977", "006249976", "006499976", "006749980",
            "006999980", "007249989", "007499989", "007749975",
            "007999975", "008249974", "008499974", "008749979",
            "008999979", "009249969", "009499969", "009749964",
            "009999964", "010249966", "010499966", "010749961",
        ) { acn ->
            val base = acn.substring(0, 8).toList().map { it.toString().toInt() }
            acnFromBase(base) shouldBe acn
        }
    }
})

fun randomKey() = Random.nextUInt().toString(16)

fun randomCrn() = Random.nextInt(1_000_000_000).toString()

fun randomTxnId() = randomKey() // UUID.randomUUID().toString()
