package net.kodehawa.mantarobot.commands.currency.entity.player;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.kodehawa.mantarobot.commands.currency.entity.Entity;
import net.kodehawa.mantarobot.commands.currency.game.core.GameReference;
import net.kodehawa.mantarobot.commands.currency.inventory.Inventory;
import net.kodehawa.mantarobot.commands.currency.inventory.ItemStack;
import net.kodehawa.mantarobot.commands.currency.inventory.TextChannelGround;
import net.kodehawa.mantarobot.data.MantaroData;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Single Guild {@link net.kodehawa.mantarobot.commands.currency.entity.Entity} wrapper.
 * This contains all the functions necessary to make the Player interact with the TextChannelGround (World).
 *
 * This is extended on {@link net.kodehawa.mantarobot.commands.currency.entity.player.EntityPlayerMP} (Global), so it also contains those objects.
 * When returned, it will return a {@link java.lang.String}  representation of all the objects here.
 *
 * The user will see a representation of this if the guild is in local mode, else it will be a representation of EntityPlayerMP
 *
 * @see net.kodehawa.mantarobot.commands.currency.entity.Entity
 * @author Kodehawa
 */
public class EntityPlayer implements Entity {
	public Map<Integer, Integer> inventory = new HashMap<>();
	private long money = 0;
	private int health = 250, stamina = 100;
	private UUID uniqueId;

	//Don't serialize this.
	private transient boolean processing;
	private transient static String entity;
	private transient GameReference currentGame;

	public EntityPlayer(){}

	/**
	 * Gets the specified EntityPlayer which needs to be seeked.
	 * @param e
	 * @return
	 */
	public static EntityPlayer getPlayer(User e){
		Objects.requireNonNull(e,  "Player user cannot be null!");
		entity = e.toString();
		return MantaroData.getData().get().getUser(e, true);
	}

	public static EntityPlayer getPlayer(String entityId){
		Objects.requireNonNull(entityId,  "Player id cannot be null!");
		entity = entityId;
		return MantaroData.getData().get().users.getOrDefault(entityId, new EntityPlayerMP());
	}

	@Override
	public int getMaxHealth(){
		return 250;
	}

	@Override
	public int getMaxStamina(){
		return 100;
	}

	@Override
	public boolean addStamina(int amount) {
		if (stamina + amount < 0 || stamina + amount > getMaxStamina()) return false;
		stamina += amount;
		return true;
	}

	@Override
	public Type getType(){
		return Type.PLAYER;
	}

	@Override
	public boolean addHealth(int amount) {
		if (health - amount < 0 || health + amount > getMaxHealth()) return false;
		health += amount;
		return true;
	}

	public boolean consumeStamina(int amount) {
		if (this.stamina - amount < 0) return false;
		return addStamina(-amount);
	}

	public boolean consumeHealth(int amount) {
		if (this.health - amount < 0) return false;
		return addHealth(-amount);
	}

	public boolean addMoney(long money) {
		try {
			this.money = Math.addExact(this.money, money);
			return true;
		} catch (ArithmeticException ignored) {
			this.money = 0;
			this.getInventory().process(new ItemStack(9, 1));
			return false;
		}
	}

	@Override
	public UUID getId() {
		return uniqueId == null ?
				uniqueId = new UUID(money * new Random().nextInt(15), System.currentTimeMillis()) : uniqueId;
	}

	@Override
	public Inventory getInventory() {
		return new Inventory(this);
	}

	public boolean removeMoney(long money) {
		if (this.money - money < 0) return false;
		this.money -= money;
		return true;
	}

	public long getMoney() {
		return money;
	}

	public void setMoney(long amount){
		money = amount;
	}

	public int getHealth() {
		return health;
	}

	public int getStamina() {
		return stamina;
	}

	public void setProcessing(boolean processing) {
		this.processing = processing;
	}

	public boolean isProcessing() {
		return processing;
	}

	public GameReference getGame(){
		return currentGame;
	}

	public void setCurrentGame(@Nullable GameReference game, TextChannel channel){
		currentGame = game;
		if(game != null) TextChannelGround.of(channel).addEntity(this, game);
		else TextChannelGround.of(channel).removeEntity(this);
	}

	@Override
	public String toString(){
		return String.format(this.getClass().getSimpleName() +
						"({type: %s, id: %s, entity: %s, money: %s, health: %s, stamina: %s, processing: %s, inventory: %s, game: %s)",
							getType(), getId(), entity, getMoney(), getHealth(), getStamina(), isProcessing(), getInventory().asList(), getGame());
	}
}