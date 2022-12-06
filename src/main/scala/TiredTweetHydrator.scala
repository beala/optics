object TiredTweetHydrator extends App {

  case class Tweet(id: String, text: Option[String])
  case class Trend(
                            trend: String,
                            tweet: Option[Tweet]
                          )

  /**
   * Fetch a list of trends from some database or remote system.
   */
  def fetchTrends(): List[Trend] = List(
    Trend("Open AI", Some(Tweet("1435631", None))),
    Trend("Elon Musk", Some(Tweet("56752", None)))
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

  def hydrateTweet(trend: Trend): Trend = {
    // Oof immutability makes this painful.
    val hydratedTweet: Option[Tweet] =
      trend.tweet.map((t: Tweet) => t.copy(text = fetchTweetText(t.id)))
    trend.copy(tweet = hydratedTweet)
  }

  val hydratedTrends = trends.map(hydrateTweet)

  println("Hydrated trends")
  hydratedTrends.foreach(println)
}
