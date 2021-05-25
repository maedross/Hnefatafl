Feature: Illegal Moves

  As a user
  I want to repeat a position three times
  So that one player must make a different move

  Rule: When a board position has been reached 3 times, the player in control must make a different move
    Scenario: Attempt to repeat a position a 4th time
      # Easier way to do this tracking state history?
      Given a game board of "repeatedMoveGiven"
          #position start
      When the attacker moves from 2, 2 to 2, 3
      When the defender moves from 3, 2 to 3, 3
      When the attacker moves from 2, 3 to 2, 2
      When the defender moves from 3, 3 to 3, 2
          #postion reached twice
      When the attacker moves from 2, 2 to 2, 3
      When the defender moves from 3, 2 to 3, 3
      When the attacker moves from 2, 3 to 2, 2
      When the defender moves from 3, 3 to 3, 2
          #position reached third time
      When the attacker moves from 2, 2 to 2, 3
      When the defender moves from 3, 2 to 3, 3
      When the attacker moves from 2, 3 to 2, 2
      When the defender moves from 3, 3 to 3, 2
          #attempt to reach position a fourth time
      Then the attacker must make a different move