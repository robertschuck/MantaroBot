package net.kodehawa.mantarobot.olddata;

import net.kodehawa.dataport.Data;
import net.kodehawa.mantarobot.commands.utils.data.BugData;
import net.kodehawa.mantarobot.utils.data.DataManager;
import net.kodehawa.mantarobot.utils.data.GsonDataManager;

/**
 * @deprecated This class need to wipe out of existance by the next build
 */
@Deprecated class MantaroOldData {
	private static final Object LOCK = new Object();

	private static DataManager<BugData> bugs;
	private static DataManager<Data> data;

	public static DataManager<BugData> getBugs() {
		if (bugs == null)
			synchronized (LOCK) {
				if (bugs == null) {
					bugs = new GsonDataManager<>(BugData.class, "bugs.json", BugData::new, true);
				}
			}
		return bugs;
	}

	public static DataManager<Data> getData() {
		if (data == null)
			synchronized (LOCK) {
				if (data == null) data = new GsonDataManager<>(Data.class, "data.json", Data::new, false);
			}
		return data;
	}
}