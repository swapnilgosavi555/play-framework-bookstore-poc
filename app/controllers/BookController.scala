package controllers

import javax.inject._
import play.api.mvc._
import models._

import scala.concurrent.{ExecutionContext, Future}

class BookController @Inject()(cc: ControllerComponents)(
  implicit ec: ExecutionContext
) extends AbstractController(cc)
    with play.api.i18n.I18nSupport {
  val booksStore = new BooksStore()

  def displayAllBooks() = Action.async {
    val books = booksStore.getAllBooks()
    books.map { book =>
      Ok(views.html.displaybook(book))
    }
  }

  def insertBook() = Action { implicit request =>
    Ok(views.html.insertbookform(booksStore.bookform))
  }

  def save() = Action.async { implicit request =>
    booksStore.bookform
      .bindFromRequest()
      .fold(
        formWithErrors => {
          Future
            .successful(BadRequest(views.html.insertbookform(formWithErrors)))

        },
        bookInfo => {
          booksStore.addBooks(bookInfo)
          Future
            .successful(Redirect(routes.BookController.displayAllBooks()))
        }
      )
  }

  def update(id: String) = Action.async { implicit request =>
    val book = booksStore.getBookById(id)
    book.flatMap { bookInfo =>
      bookInfo match {
        case Some(bookDetail) =>
          val updateData =
            booksStore.bookform.fill(
              Book(bookDetail.id, bookDetail.bookName, bookDetail.author)
            )
          Future.successful(Ok(views.html.updateform(id, updateData)))
        case None =>
          Future.successful(BadRequest(s"Book not found of id:$id"))
      }
    }
  }

  def edit(id: String) = Action { implicit request =>
    val book = booksStore.bookform.bindFromRequest.get
    booksStore.update(id, book)
    Redirect(routes.BookController.displayAllBooks())
  }
  def delete(id: String) = Action { implicit request =>
    booksStore.delete(id)
    Redirect(routes.BookController.displayAllBooks())
  }

}
