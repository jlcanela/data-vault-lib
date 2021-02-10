package acceptance
/*
import zio._
import zio.console._
import zio.test._
import zio.test.Assertion._
import zio.test.environment._

import zio.test.junit.JUnitRunnableSpec
import org.junit.runner.RunWith

import HelloWorld._

object HelloWorld {
  def sayHello: URIO[Console, Unit] =
    console.putStrLn("Hello, World!")
}

import java.util.concurrent.TimeUnit
import zio.clock.currentTime
import zio.duration._
import zio.test.Assertion.isGreaterThanEqualTo
import zio.test._
import zio.test.environment.TestClock

testM("One can move time very fast") {
  for {
    startTime <- currentTime(TimeUnit.SECONDS)
    _         <- TestClock.adjust(1.minute)
    endTime   <- currentTime(TimeUnit.SECONDS)
  } yield assert(endTime - startTime)(isGreaterThanEqualTo(60L))
}

class HelloWorldSpec extends DefaultRunnableSpec {
  def spec = suite("HelloWorldSpec")(
    testM("sayHello correctly displays output") {
      for {
        _      <- sayHello
        output <- TestConsole.output
      } yield assert(output)(equalTo(Vector("Hello, World!\n")))
    }
  )
}
 */
