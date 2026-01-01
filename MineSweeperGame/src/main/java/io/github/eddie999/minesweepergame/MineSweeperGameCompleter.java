package io.github.eddie999.minesweepergame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MineSweeperGameCompleter extends MineSweeperGamePermissions implements TabCompleter{
	public static final String[] ARGS0 = { "create", "help", "list", "power", "remove", "rename", "reward", "starters",};
	private static List<String> argsList0 = Arrays.asList( ARGS0);
	private static final String[] ARGS2 = { "custom", "easy", "normal", "difficult"};
	private static List<String> argsList2 = Arrays.asList( ARGS2);
	private static final String[] ARGS3 = { "player", "game"};
	private static List<String> argsList3 = Arrays.asList( ARGS3);
	private static final String[] ARGS4 = { "<player/game>", "<game>"};
	private static List<String> argsList4 = Arrays.asList( ARGS4);
	private static final String[] ARGS5 = { "item", "cash"};
	private static List<String> argsList5 = Arrays.asList( ARGS5);

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, Command command,
			String label, @NotNull String @NotNull [] args) {
		List<String> completions = new ArrayList<>();
		if( !(sender instanceof Player)) return completions;
		Player player = (Player) sender;
		
		if( args.length == 1) StringUtil.copyPartialMatches(args[0], checkPermissions(player,  argsList0), completions);
		else if( (args.length == 2) && args[0].equalsIgnoreCase("create")) completions.add("<game>");
		else if( (args.length == 2) && args[0].equalsIgnoreCase("remove")) StringUtil.copyPartialMatches(args[1], argsList3, completions);
		else if( (args.length == 2) && args[0].equalsIgnoreCase("remove")) completions.add("game");
		else if( (args.length == 2) && args[0].equalsIgnoreCase("list") && isAdmin(player)) completions.add("all");
		else if( (args.length == 2) && args[0].equalsIgnoreCase("reward")) completions.add("<game>");
		else if( (args.length == 2) && args[0].equalsIgnoreCase("rename")) completions.add("<game>");
		else if( (args.length == 2) && args[0].equalsIgnoreCase("power")) completions.add("<game>");
		else if( (args.length == 2) && args[0].equalsIgnoreCase("starters")) completions.add("<game>");
		else if( (args.length == 2) && args[0].equalsIgnoreCase("help")) StringUtil.copyPartialMatches(args[1], checkPermissions(player,  argsList0), completions);
		else if( (args.length == 3) && args[0].equalsIgnoreCase("reward") && isAdmin(player) && hasVault()) StringUtil.copyPartialMatches(args[2], argsList5, completions);
		else if( (args.length == 3) && args[0].equalsIgnoreCase("reward")) completions.add("item");
		else if( (args.length == 3) && args[0].equalsIgnoreCase("create")) StringUtil.copyPartialMatches(args[2], argsList2, completions);
		else if( (args.length == 3) && args[0].equalsIgnoreCase("remove") && args[1].equalsIgnoreCase("game") && isAdmin(player)) StringUtil.copyPartialMatches(args[2], argsList4, completions);
		else if( (args.length == 3) && args[0].equalsIgnoreCase("remove") && args[1].equalsIgnoreCase("game")) completions.add("<game>");
		else if( (args.length == 3) && args[0].equalsIgnoreCase("remove") && args[1].equalsIgnoreCase("player") && isAdmin(player)) completions.add("<player>");
		else if( (args.length == 3) && args[0].equalsIgnoreCase("power")) completions.add("<power>");
		else if( (args.length == 3) && args[0].equalsIgnoreCase("starters")) completions.add("<starter count>");
		else if( (args.length == 4) && args[0].equalsIgnoreCase("create") && args[2].equalsIgnoreCase("custom")) completions.add("<rows>");
		else if( (args.length == 4) && args[0].equalsIgnoreCase("reward") && args[2].equalsIgnoreCase("cash") && isAdmin(player) && hasVault()) completions.add("<amount>");
		else if( (args.length == 5) && args[0].equalsIgnoreCase("create") && args[2].equalsIgnoreCase("custom")) completions.add("<cols>");
		else if( (args.length == 6) && args[0].equalsIgnoreCase("create") && args[2].equalsIgnoreCase("custom")) completions.add("<mines>");
		
		return completions;
	}
}
