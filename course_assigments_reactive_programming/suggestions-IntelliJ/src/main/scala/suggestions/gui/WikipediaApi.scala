package suggestions
package gui

import scala.language.postfixOps
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters._
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Try, Success, Failure }
import rx.subscriptions.CompositeSubscription
import rx.lang.scala.{Subscription, Observable}
import observablex._
import search._
import rx.lang.scala.Notification.{OnCompleted, OnError, OnNext}
import rx.lang.scala.subjects.{ReplaySubject, PublishSubject}
import rx.lang.scala.subscriptions.BooleanSubscription

trait WikipediaApi {

  /** Returns a `Future` with a list of possible completions for a search `term`.
   */
  def wikipediaSuggestion(term: String): Future[List[String]]

  /** Returns a `Future` with the contents of the Wikipedia page for the given search `term`.
   */
  def wikipediaPage(term: String): Future[String]

  /** Returns an `Observable` with a list of possible completions for a search `term`.
   */
  def wikiSuggestResponseStream(term: String): Observable[List[String]] = ObservableEx(wikipediaSuggestion(term))

  /** Returns an `Observable` with the contents of the Wikipedia page for the given search `term`.
   */
  def wikiPageResponseStream(term: String): Observable[String] = ObservableEx(wikipediaPage(term))

  implicit class StringObservableOps(obs: Observable[String]) {

    /** Given a stream of search terms, returns a stream of search terms with spaces replaced by underscores.
     *
     * E.g. `"erik", "erik meijer", "martin` should become `"erik", "erik_meijer", "martin"`
     */
    def sanitized: Observable[String] = obs.map(_.replace(' ', '_'))
//      Observable(observer => {
//      obs.subscribe(
//        value => observer.onNext(value.replace(' ', '_')),
//        error => observer.onError(error),
//        () => observer.onCompleted()
//      )
//    })
  }

  implicit class ObservableOps[T](obs: Observable[T]) {

    /** Given an observable that can possibly be completed with an error, returns a new observable
     * with the same values wrapped into `Success` and the potential error wrapped into `Failure`.
     *
     * E.g. `1, 2, 3, !Exception!` should become `Success(1), Success(2), Success(3), Failure(Exception), !TerminateStream!`
     */
    def recovered: Observable[Try[T]] = {
      val subject = ReplaySubject[Try[T]]()

      obs.subscribe(
        value => subject.onNext(Success(value)),
        error => {
          subject.onNext(Failure(error))
          subject.onCompleted()
        },
        () => subject.onCompleted()
      )

      subject
    }


//    def recovered: Observable[Try[T]] = obs.materialize.filter {
//      case OnCompleted() => false
//      case _ => true
//    }.map{
//      case OnNext(value) => Success(value)
//      case OnError(error) => Failure(error)
//    }

//    def recovered: Observable[Try[T]] = obs.materialize.map{
//      case OnNext(value) => Success(value)
//      case OnError(error) => Failure(error)
//    }
//
    /** Emits the events from the `obs` observable, until `totalSec` seconds have elapsed.
     *
     * After `totalSec` seconds, if `obs` is not yet completed, the result observable becomes completed.
     *
     * Note: uses the existing combinators on observables.
     */
    def timedOut(totalSec: Long): Observable[T] = {
      val subject = ReplaySubject[T]()

      val sourceSubs = obs.subscribe(
        value =>subject.onNext(value),
        error => subject.onError(error),
        () => subject.onCompleted()
      )

      Observable.interval(totalSec seconds).first.subscribe(
        value => {
          sourceSubs.unsubscribe()
          subject.onCompleted()
        }
      )
      subject
    }

//    def timedOut(totalSec: Long): Observable[T] = {
//      val subject = ReplaySubject[T]()
//
//      val sourceSubs = obs.subscribe(
//        value =>subject.onNext(value),
//        error => subject.onError(error),
//        () => subject.onCompleted()
//      )
//
//      Observable.interval(1000 millis).take(totalSec.toInt).subscribe(
//        value => {
//          if (value + 1 >= totalSec){
//            sourceSubs.unsubscribe()
//            subject.onCompleted()
//          }
//        }
//      )
//      subject
//    }
//
    /** Given a stream of events `obs` and a method `requestMethod` to map a request `T` into
     * a stream of responses `S`, returns a stream of all the responses wrapped into a `Try`.
     * The elements of the response stream should reflect the order of their corresponding events in `obs`.
     *
     * E.g. given a request stream:
     *
     * 1, 2, 3, 4, 5
     *
     * And a request method:
     *
     * num => if (num != 4) Observable.just(num) else Observable.error(new Exception)
     *
     * We should, for example, get:
     *
     * Success(1), Success(2), Success(3), Failure(new Exception), Success(5)
     *
     *
     * Similarly:
     *
     * Observable(1, 2, 3).concatRecovered(num => Observable(num, num, num))
     *
     * should return:
     *
     * Observable(1, 1, 1, 2, 2, 2, 3, 3, 3)
     */
    def concatRecovered[S](requestMethod: T => Observable[S]): Observable[Try[S]] = {
      val nested = obs.map{t=>
        requestMethod(t).recovered
      }

      nested.concat
    }
  }

}

