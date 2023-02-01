package io.bitbucket.plt.autotutor;// Generated from java-escape by ANTLR 4.11.1

	import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link DrRacketParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface DrRacketVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link DrRacketParser#start}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStart(DrRacketParser.StartContext ctx);
	/**
	 * Visit a parse tree produced by {@link DrRacketParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(DrRacketParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link DrRacketParser#terminal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerminal(DrRacketParser.TerminalContext ctx);
	/**
	 * Visit a parse tree produced by {@link DrRacketParser#string_terminal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString_terminal(DrRacketParser.String_terminalContext ctx);
	/**
	 * Visit a parse tree produced by {@link DrRacketParser#hash_terminal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHash_terminal(DrRacketParser.Hash_terminalContext ctx);
	/**
	 * Visit a parse tree produced by {@link DrRacketParser#true_terminal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTrue_terminal(DrRacketParser.True_terminalContext ctx);
	/**
	 * Visit a parse tree produced by {@link DrRacketParser#false_terminal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFalse_terminal(DrRacketParser.False_terminalContext ctx);
	/**
	 * Visit a parse tree produced by {@link DrRacketParser#symbol_terminal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSymbol_terminal(DrRacketParser.Symbol_terminalContext ctx);
	/**
	 * Visit a parse tree produced by {@link DrRacketParser#lambda_terminal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLambda_terminal(DrRacketParser.Lambda_terminalContext ctx);
	/**
	 * Visit a parse tree produced by {@link DrRacketParser#name_terminal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName_terminal(DrRacketParser.Name_terminalContext ctx);
	/**
	 * Visit a parse tree produced by {@link DrRacketParser#number_terminal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber_terminal(DrRacketParser.Number_terminalContext ctx);
	/**
	 * Visit a parse tree produced by {@link DrRacketParser#character_terminal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCharacter_terminal(DrRacketParser.Character_terminalContext ctx);
	/**
	 * Visit a parse tree produced by {@link DrRacketParser#round_paren}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRound_paren(DrRacketParser.Round_parenContext ctx);
	/**
	 * Visit a parse tree produced by {@link DrRacketParser#square_paren}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSquare_paren(DrRacketParser.Square_parenContext ctx);
	/**
	 * Visit a parse tree produced by {@link DrRacketParser#quote}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuote(DrRacketParser.QuoteContext ctx);
	/**
	 * Visit a parse tree produced by {@link DrRacketParser#quasiquote}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuasiquote(DrRacketParser.QuasiquoteContext ctx);
	/**
	 * Visit a parse tree produced by {@link DrRacketParser#unquote}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnquote(DrRacketParser.UnquoteContext ctx);
	/**
	 * Visit a parse tree produced by {@link DrRacketParser#vector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVector(DrRacketParser.VectorContext ctx);
}