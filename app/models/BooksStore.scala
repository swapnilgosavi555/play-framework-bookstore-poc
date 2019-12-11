package models

import play.api.data._
import play.api.data.Forms._
import reactivemongo.bson.{BSONDocument, BSONHandler, Macros}
import reactivemongo.api.{MongoDriver, ReadPreference}
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.Cursor.FailOnError

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.Await

case class Book(id: String, bookName: String, author: String)

class BooksStore() {
  val driver = new MongoDriver
  val connection = driver.connection(List("localhost"))
  val database = Await.result(connection.database("bookstore"), 10.seconds)
  val collection = database.collection[BSONCollection]("bookrecord")

  implicit val bookreads = Macros.reader[Book]
  implicit val bookWrites = Macros.writer[Book]

  val bookform = Form(
    mapping(
      "id" -> nonEmptyText,
      "bookName" -> nonEmptyText,
      "author" -> nonEmptyText
    )(Book.apply)(Book.unapply)
  )

  def addBooks(book: Book) = {
    val document = BSONDocument(
      "id" -> book.id,
      "bookName" -> book.bookName,
      "author" -> book.author
    )
    collection.insert(document)
  }
  def getAllBooks() = {
    collection
      .find(BSONDocument())
      .cursor[Book](ReadPreference.Primary)
      .collect[List](-1, FailOnError[List[Book]]())
  }

  def update(id: String, book: Book) = {
    val selector = BSONDocument("id" -> id)
    val modifier =
      BSONDocument(
        "id" -> id,
        "bookName" -> book.bookName,
        "author" -> book.author
      )
    collection.update(selector, modifier)
  }
  def delete(id: String) = {
    collection.remove(BSONDocument("id" -> id))
  }
  def getBookById(id: String) = {
    collection.find(BSONDocument("id" -> id)).one[Book]
  }
}
