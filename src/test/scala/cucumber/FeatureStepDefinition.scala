package steps

import io.cucumber.scala.ScalaDsl
import io.cucumber.scala.EN
import org.scalatest.Matchers



final class FeaturesStepDefinitions extends ScalaDsl with EN with Matchers {
  // ...

  //val conferenceId = "mix-it-18"
  //var conferenceEvents: Seq[Event] = _

  Given("""^a conference with a quota of (\d+) "([a-zA-Z]+)" seats$""") { (quota: Int, seatType: String) =>
    /*this.conferenceEvents = Seq(
      ConferenceCreated(name = "MixIT", slug = conferenceId),
      SeatsAdded(conferenceId, seatType, quota),
      ConferencePublished(id = conferenceId)
    )

    eventSourcedRepository.setHistory(conferenceId, conferenceEvents: _*)*/
  }

  When("""^a registrant place an order for (\d+) "([a-zA-Z]+)" seats$""") { (seatsRequest: Int, seatType: String) =>
    //orderCommandHandler handle PlaceOrder(conferenceId, seatType -> seatsRequest)
  }

  Then("""^the (\d+) "([a-zA-Z]+)" seats are successfully reserved$""") { (seatsRequest: Int, seatType: String) =>
    /*eventSourcedRepository.getEventStream(conferenceId) should contain theSameElementsInOrderAs (
      conferenceEvents :+ SeatsReserved(conferenceId, orderId = "ID-1", seatType -> seatsRequest)
    )

    eventSourcedRepository.getEventStream("ID-1") should contain inOrderOnly(
      OrderPlaced(orderId = "ID-1", conferenceId, seatType -> seatsRequest),
      SeatsReservationConfirmed(orderId = "ID-1", seatType -> seatsRequest)
    )*/
  }
}