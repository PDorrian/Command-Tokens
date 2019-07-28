package me.bukkit.quib;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.vexsoftware.votifier.model.VotifierEvent;
import com.vexsoftware.votifier.model.Vote;

import java.io.*;
import java.util.*;

public class Appel extends JavaPlugin implements Listener {
    HashMap<String, Integer> map = new HashMap<String, Integer>();

    @Override
    public void onEnable() {
        getLogger().info("Appel Plugin Successfully Enabled");
        getServer().getPluginManager().registerEvents(this, this);

        map = loadMap();
    }

    @Override
    public void onDisable() {
        saveMap(map);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("discord") && sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage("Come join our Discord Server : " + ChatColor.RED + "https://discord.gg/Dv3qhgE");
            return true;
        }

        if (command.getName().equalsIgnoreCase("vote") && sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage("Vote for the Server : " + ChatColor.RED + "https://discord.gg/Dv3qhgE");
            return true;
        }

        if (command.getName().equalsIgnoreCase("givetoken") && sender instanceof Player) {
            addToken(args[0]);
            sender.sendMessage("A token has been added to " + args[0] + "'s account. They have " + map.get(args[0]) + " warp tokens.");
            return true;
        }

        if (command.getName().equalsIgnoreCase("taketoken") && sender instanceof Player) {
            removeToken(args[0]);
            sender.sendMessage("A token has been removed from " + args[0] + "'s account. They have " + map.get(args[0]) + " warp tokens remaining.");
            return true;
        }

        if (command.getName().equalsIgnoreCase("tokens") && sender instanceof Player) {
            sender.sendMessage(args[0] + " has " + map.get(args[0]) + " warp tokens.");
            return true;
        }

        return false;
    }

    @EventHandler
    public void onVote(VotifierEvent e) {
        Vote v = e.getVote();
        String name = v.getUsername();

        Bukkit.getServer().broadcastMessage("Thank you for voting, " + name + "! You have been awarded 1 Warp Token.");
        addToken(name);

        System.out.println(name + " has voted for the server.");
    }

    @EventHandler
    public void commandPreprocess(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        String[] array = message.split(" ");

        if(array[0].equalsIgnoreCase("/tp")) {
            String player = event.getPlayer().getName();

            if (map.get(player) > 0) {
                removeToken(player);
                event.getPlayer().sendMessage("One warp token has been removed from your account. Tokens remaining : " + map.get(player));
            } else {
                event.getPlayer().sendMessage("You don't have any Warp Tokens! Do " + ChatColor.RED + "/vote" + ChatColor.WHITE + " to vote for us daily.");
                event.setCancelled(true);
            }

        }
    }

    public void addToken(String name) {
        if (map.containsKey(name)) {
            map.put(name, map.get(name) + 1);
        } else {
            map.put(name, 1);
        }

        saveMap(map);
    }

    public void removeToken(String name) {
        if (map.containsKey(name) && map.get(name) > 0) {
            map.put(name, map.get(name) - 1);
        } else {
            System.out.println("Tried to remove a token while balance is empty. This should be impossible so pls let Quib know.");
        }

        saveMap(map);
    }

    public void saveMap(HashMap<String, Integer> hmap) {
        try
        {
            FileOutputStream fos = new FileOutputStream("warptokens.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(hmap);
            oos.close();
            fos.close();
            System.out.printf("warptoken.ser has been updated");
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public HashMap<String, Integer> loadMap() {
        HashMap<String, Integer> hmap = null;
        try
        {
            FileInputStream fis = new FileInputStream("warptokens.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            hmap = (HashMap) ois.readObject();
            ois.close();
            fis.close();
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
            return null;
        }catch(ClassNotFoundException c)
        {
            System.out.println("Class not found");
            c.printStackTrace();
            return null;
        }
        System.out.println("Deserialized HashMap..");
        // Display content using Iterator
        Set set = hmap.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry)iterator.next();
            System.out.print("key: "+ mentry.getKey() + " & Value: ");
            System.out.println(mentry.getValue());
        }

        return hmap;
    }
}
