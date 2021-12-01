package game

// This contains literals for the story. Not the most elegant solution, but the easiest
object Story {
  val driver_intro_1 = "As you are sitting by the side of the road, a customer knocks on your window."

  def customer_intro_line_1(str1: String, str1_card: String, str2: String, str2_card: String) = s"I'm in a hurry, can you get me to the corner of ${str1} (${str1_card}) and ${str2} (${str2_card})"

}
