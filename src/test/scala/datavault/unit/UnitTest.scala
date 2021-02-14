package datavault.unit

import zio._
import zio.test.Assertion._
import zio.test._
import zio.stream._

import zio.console.Console
import java.nio.file.{Files, Paths}
import datavault.Datavault
import datavault.extractload.ExtractLoad
import ExtractLoad.Table

object UnitTest extends DefaultRunnableSpec {

  val deps = Datavault.deps

  def run(args: Array[String]): Unit = Datavault.main(args)

  def spec = suite("Datavault unit tests")(
    test("no parameters should return ()") {
      assert(run(Array()))(equalTo(()))
    },
    testM("list files returns 9 files") {
      def show(file: ExtractLoad.File): ZIO[Console, Nothing, Unit] = console.putStrLn(file.toString)
      val showall = Sink.foreach(show)

      def countFiles(path: String): ZIO[ExtractLoad with Console, Throwable, Long] = for {
        st <- ExtractLoad.archiveFiles(Paths.get(path))
         count <- st.run(Sink.count)
      } yield count

      assertM(countFiles("data/archive.zip").provideLayer(Datavault.deps))(equalTo(9L))
    },
    testM("table extract the headers") {
      def stream = IO(Files.newInputStream(Paths.get("src/test/resources/fixtures/robustness.csv")))
      def headers = for {
        st <- stream 
        table <- ExtractLoad.table(st)
      } yield table.headers.toList
      assertM(headers.provideLayer(Datavault.deps))(equalTo(List("Year", "Make", "Model", "Description", "Price")))
    },
    testM("table extract rows") {
      def stream = IO(Files.newInputStream(Paths.get("src/test/resources/fixtures/robustness.csv")))
         def rows = for {
        st <- stream 
        table <- ExtractLoad.table(st)
        rows = table.rows
        count <- rows.run(Sink.count)
      } yield count
      assertM(rows.provideLayer(Datavault.deps))(equalTo(5L))
    },
    testM("tables extract rows") {
      val expectedHeaders = List(
        List("product_category_name", "product_category_name_english"), 
        List("seller_id", "seller_zip_code_prefix", "seller_city", "seller_state"), 
        List("product_id", "product_category_name", "product_name_lenght", "product_description_lenght", "product_photos_qty", "product_weight_g", "product_length_cm", "product_height_cm", "product_width_cm"), 
        List("order_id", "customer_id", "order_status", "order_purchase_timestamp", "order_approved_at", "order_delivered_carrier_date", "order_delivered_customer_date", "order_estimated_delivery_date"), 
        List("review_id", "order_id", "review_score", "review_comment_title", "review_comment_message", "review_creation_date", "review_answer_timestamp"), 
        List("order_id", "payment_sequential", "payment_type", "payment_installments", "payment_value"), 
        List("order_id", "order_item_id", "product_id", "seller_id", "shipping_limit_date", "price", "freight_value"), 
        List("geolocation_zip_code_prefix", "geolocation_lat", "geolocation_lng", "geolocation_city", "geolocation_state"), 
        List("customer_id", "customer_unique_id", "customer_zip_code_prefix", "customer_city", "customer_state"))
      def allHeaders = for {
        tables <- ExtractLoad.tables(Paths.get("data/archive.zip"))
        sink = Sink.foldLeft((List[List[String]]()))((list: List[List[String]], table: Table) => table.headers.toList :: list )
        all <- tables.run(sink)
      } yield all
      assertM(allHeaders.provideLayer(Datavault.deps))(equalTo(expectedHeaders))
    },
    testM("table write to disk") {
      val table = Table(Array("col1", "col2"), Stream(Array("r1col1", "r1col2"), Array("r2col1", "r2col2")))
      assertM(table.write(Paths.get("out/export/table.csv")))(equalTo(()))
    }
  )
}
