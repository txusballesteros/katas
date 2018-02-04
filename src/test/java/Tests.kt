// Copyright © FunctionalHub.com 2018. All rights reserved.

import functional.transforms.BoxArt
import functional.transforms.castsDemo
import functional.transforms.filtermap.getFiveRatingMoviesIds
import functional.transforms.flatMap.advanced.getAllMoviesInformation
import functional.transforms.flatMap.simple.getAllMoviesIds
import functional.transforms.map.getMovies
import functional.transforms.mapreduce.getUrlOfLargestBoxArt
import functional.transforms.movieListsDemo
import functional.transforms.moviesDemo
import functional.transforms.reduce.getLargestRating
import functional.transforms.zip.getFirstActorMap
import functions.cps.FailureException
import functions.cps.performOperationCPS
import functions.currying.add
import functions.currying.curried
import functions.partial.Element
import functions.partial.partial
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldThrow
import io.kotlintest.specs.FreeSpec
import recursion.adding.multiplying.canReach
import recursion.fibonacci.fibonacci
import recursion.reduce.reduceKt

class Tests : FreeSpec() {
    init {
        "1 - Recursion" - {
            "ReduceKata" - {
                "reduceKt with String::plus should join all strings" {
                    val list = listOf("functional", "hub", ".", "com")

                    val reduced = list.reduceKt(String::plus)

                    reduced shouldBe "functionalhub.com"
                }
                "reduceKt should calculate factorial" {
                    val factorial = (1..10).toList().reduceKt(Int::times)

                    factorial shouldBe 3628800
                }
                "reduceKt should fail if empty list" {
                    shouldThrow<UnsupportedOperationException> {
                        emptyList<String>().reduceKt(String::plus)
                    }
                }
            }
            "AddingMultiplyingKata" - {
                "canReach should work for certain combinations" {
                    canReach(59, 5, 7) shouldBe true
                    canReach(1, 5, 5) shouldBe true
                    canReach(10, 5, 3) shouldBe false
                    canReach(100, 1, 1) shouldBe true
                }
                "canReach should return same result as verified function" {
                    fun testCanReach(n: Int, add: Int, multiply: Int): Boolean =
                        if (n <= 1) {
                            n == 1
                        } else {
                            if (n % multiply == 0 && multiply > 1) {
                                testCanReach(n / multiply, add, multiply) ||
                                    testCanReach(n - add, add, multiply)
                            } else {
                                testCanReach(n - add, add, multiply)
                            }
                        }

                    (0..50).forEach {
                        val n = (1..2000).random()
                        val add = (1..20).random()
                        val multiply = (1..20).random()

                        canReach(n, add, multiply) shouldBe
                            testCanReach(n, add, multiply)
                    }

                }
            }
            "Fibonacci" - {
                "fibonacci should return fibonacci number in position" {
                    val fibonacciList = listOf(
                        1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377,
                        610, 987)

                    fibonacciList.forEachIndexed { index, number ->
                        fibonacci(index) shouldBe number
                    }
                }
            }
        }
        "2 - Functions" - {
            "Currying" - {
                "curried function should curry any bi-function" {
                    ::add.curried()(1)(3) shouldBe 4
                    String::plus.curried()("He")("llo") shouldBe "Hello"
                }
            }
            "Partial" - {
                "partial must return a partially applied constructor" {
                    partial(3) shouldBe Element("Blog", "Fixed", 3)
                    partial(42) shouldBe Element("Blog", "Fixed", 42)
                }
            }
            "CPS" - {
                "performOperationCPS should call onSuccess if true" {
                    var result = 0

                    performOperationCPS(
                        { true },
                        { int: Int -> result = int},
                        { throw AssertionError("Shouldn't be called") })

                    result shouldBe 42
                }
                "performOperationCPS should call onFailure if false" {
                    var result = Exception()

                    performOperationCPS(
                        { false },
                        { throw AssertionError("Shouldn't be called")},
                        { ex: Exception -> result = ex })

                    result shouldBe FailureException("Error")
                }
            }
        }
        "3 - Functional transforms" - {
            "Map" - {
                "getMovies should return valid map" {
                    getMovies(moviesDemo) shouldBe
                        moviesDemo.map { it.id to it.title }.toMap()
                }
            }
            "Map & Filter" - {
                "getFiveRatingMoviesIds should return valid list" {
                    getFiveRatingMoviesIds(moviesDemo) shouldBe moviesDemo.filter {
                        it.rating == 5.0
                    }.map {
                        it.id
                    }
                }
            }
            "FlatMap" - {
                "Simple" - {
                    "getAllMoviesIds should return valid list" {
                        getAllMoviesIds(movieListsDemo) shouldBe
                            moviesDemo.map { it.id }
                    }
                }
                "Advanced" - {
                    "getAllMoviesInformation should return valid list" {
                        getAllMoviesInformation(movieListsDemo) shouldBe
                            moviesDemo.map {
                                mapOf(
                                    "id" to it.id, "title" to it.title,
                                    "boxArt" to it.boxArts.boxArt150())
                            }
                    }
                }
            }
            "Reduce" - {
                "getLargestRating should return largest rating" {
                    getLargestRating(moviesDemo) shouldBe 5.0
                }
            }
            "MapReduce" - {
                "getUrlOfLargestBoxArt should return valid url" {
                    getUrlOfLargestBoxArt(moviesDemo) shouldBe
                        moviesDemo.flatMap {
                            it.boxArts
                        }.biggestBoxArt().url
                }
            }
            "Zip" - {
                "getFirstActorMap returns a map with the first actor names" {
                    getFirstActorMap(moviesDemo, castsDemo) shouldBe moviesDemo
                        .zip(castsDemo)
                        .map { (movie, cast) ->
                            movie.title to cast.nameOfFirstActor()
                        }.toMap()
                }
            }
        }
    }
}
