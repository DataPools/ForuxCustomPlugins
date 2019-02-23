package net.richardsprojects.foruxteams;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Utils {

	public static String convertListToString(List<UUID> list) {
		String result = "";
		
		List<UUID> newList = new ArrayList<>(list);
		for (UUID uuid : newList) {
			result = result + uuid.toString() + ",";
		}
		if (result.length() > 0) result = result.substring(0, result.length() - 1);
		
		return result;
	}
	
	public static List<UUID> convertStringToList(String str) {
		ArrayList<UUID> list = new ArrayList<UUID>();
		
		for (String tmp : str.split(",")) {
			try {
				UUID current = UUID.fromString(tmp);
				list.add(current);
			} catch (Exception e) {
				
			}
		}
		
		return list;
	}
	
}
