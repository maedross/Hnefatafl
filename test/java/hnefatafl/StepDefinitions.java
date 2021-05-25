package hnefatafl;

import hnefatafl.Hnefatafl;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.*;

public class StepDefinitions {
    @Given("the menu")
    public void theMenu() {
        // Probably just run the startup?
    }

    @Given("a game board of {string}")
    public void aGameBoardOf(String arg0) {
        // Load in the appropriate file and decode the string
        // Need to generate path somewhere? Should we specify whether it's a save file vs a testing board
        // since they might be in different locations? Or just put them in the same location?
        String givenBoard = Hnefatafl.loadEncodedBoard(arg0);
        Hnefatafl.setBoard(givenBoard);
    }

    @When("I start the game")
    public void iStartTheGame() {
        // Select new game
        Hnefatafl.newGame();
    }

    @When("the {string} moves from {int}, {int} to {int}, {int}")
    public void thePlayerMovesFromTo(String arg0, int arg1, int arg2, int arg3, int arg4) {
        Hnefatafl.move(arg0, arg1, arg2, arg3, arg4);
    }

    @Then("the game board state is {string}")
    public void theGameBoardStateIs(String arg0) {
        // Encode current board, load appropriate file, compare strings
        // Need to generate path somewhere? Should we specify whether it's a save file vs a testing board
        // since they might be in different locations? Or just put them in the same location?
        String expectedBoard = Hnefatafl.loadEncodedBoard(arg0);
        assertTrue(Hnefatafl.compareBoards(expectedBoard));
    }

    @Then("it is the attacker's turn")
    public void itIsTheAttackerSTurn() {
        // Assert?
    }

    @Then("the attacker must make a different move")
    public void theAttackerMustMakeADifferentMove() {
        System.out.println("Invalid move: you must make a different move");
    }

    @Then("the attacker wins")
    public void theAttackerWins() {
        // Show attacker victory screen
        // Assert?
    }

    @Then("the defender wins")
    public void theDefenderWins() {
        // Show defender victory screen
        // Assert?
    }
}
