package net.just_s.ctpmod;

public class CMDHandler {
    public static final CTPMod mod = new CTPMod();

    public static void cmdHandler(String[] args) {
        String message = "";
        boolean error = false;
        try {
            String command = args[0];
            switch (command) {
                case "add" -> mod.addPoint(args);
                case "del" -> mod.delPoint(args);
                case "tp" -> mod.teleport(args);
                case "list" -> mod.listPoints();
            }
            error = true;
        }
        catch (NumberFormatException e) { message = "bad Period argument. §c" + e;}
        catch (ArrayIndexOutOfBoundsException  e) { message = "wrong number of arguments. §c" + e;}
        catch (ClassCastException  e) { message = "wrong type of argument. §c" + e;}
        catch (IllegalArgumentException e) { message = "bad writing. §c" + e;}

        if (!error) {
            CTPMod.printInGame(message);
        }
    }
}
