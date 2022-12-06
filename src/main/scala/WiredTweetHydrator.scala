import Utils.assertEquals
import monocle.macros.Lenses
import monocle.{Lens, Optional}

object WiredTweetHydrator extends App {
  @Lenses("_") case class Tweet(id: String, text: Option[String])
  @Lenses("_") case class Trend(
                            trend: String,
                            tweet: Option[Tweet]
                          )

  val trend1 = Trend("Open AI", Some(Tweet("1435631", None)))
  val trend2 = Trend("Elon Musk", Some(Tweet("56752", None)))
  /**
   * Fetch a list of trends from some database or remote system.
   */
  def fetchTrends(): List[Trend] = List(
    trend1,
    trend2
  )

  // List of trends from the trends service
  val trends = fetchTrends()

  println("Unhydrated trends")
  trends.foreach(println)
  println()

  def fetchTweetText(id: String): Option[String] = {
    Map.apply(
      "1435631" -> "I typed my CSCI 3155 homework into ChatGPT and here are the results:",
      "56752" -> "Tech workers workers nervous after Elon fires all but 10 engineers with no impact to Twitter’s uptime."
    ).get(id)
  }

  val trendTweetLens: Lens[Trend, Option[Tweet]] = Trend._tweet
  val trendTweetGetter: Trend => Option[Tweet] = trendTweetLens.get
  assertEquals(
    trendTweetGetter(trend1),
    Some(Tweet("1435631", None))
  )
  val trendTweetSetter: Option[Tweet] => Trend => Trend = trendTweetLens.replace
  assertEquals(
    trendTweetSetter(Some(Tweet("0", None)))(trend1),
    Trend("Open AI", Some(Tweet("0", None)))
  )
  private val trendTweetSetterOption: Tweet => Trend => Trend = trendTweetLens.some.replace
  assertEquals(
    trendTweetSetterOption(Tweet("0", None))(trend1),
    Trend("Open AI", Some(Tweet("0", None)))
  )

  val tweetTextLens: Lens[Tweet, Option[String]] = Tweet._text

  val tweetText: Optional[Trend, String] = trendTweetLens.some.andThen(tweetTextLens).some
  val tweetTextExpanded: Optional[Trend, String] = Trend._tweet.some.andThen(Tweet._text.some)
  assertEquals(
    tweetText.getOption(trend1),
    None
  )
  assertEquals(
    tweetText.getOption(Trend("Open AI", Some(Tweet("1435631", Some("I typed my CSCI 3155 homework into ChatGPT and here are the results:"))))),
    Some("I typed my CSCI 3155 homework into ChatGPT and here are the results:")
  )

  def hydrateTweet(trend: Trend): Trend =
    Trend._tweet.some.modify(t => Tweet._text.replace(fetchTweetText(t.id))(t))(trend)

  trends.map(hydrateTweet)

  import cats.implicits._
  import monocle.Traversal

  val focusOnAllTrends: Traversal[List[Trend], Trend] = Traversal.fromTraverse[List, Trend]
  def hydrateTweet2(trends: List[Trend]): List[Trend] =
    focusOnAllTrends.andThen(Trend._tweet.some).modify(t => Tweet._text.replace(fetchTweetText(t.id))(t))(trends)

  println("Hydrated tweets")
  hydrateTweet2(trends).foreach(println)
}
