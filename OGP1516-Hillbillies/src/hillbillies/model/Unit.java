
package hillbillies.model;

import java.util.LinkedList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import be.kuleuven.cs.som.annotate.*;
import ogp.framework.util.ModelException;

/**
 *	A class describing the Hillbillie Unit
 *
 * @invar The Weight of each Unit must be a valid Weight for any Unit. 
 * 			| isValidWeight(getWeight())
 * @invar The Agility of each Unit must be a valid Agility for any Unit. 
 * 			| isValidAgility(getAgility())
 * @invar The Strength of each Unit must be a valid Strength for any Unit. 
 * 			| isValidStrength(getStrength())
 * @invar The Toughness of each Unit must be a valid Toughness for any Unit.
 * 			| isValidToughness(getToughness())
 * @invar The Hitpoints of each Unit must be a valid Hitpoints for any Unit. 
 * 			| isValidHitpoints(getHitpoints())
 * @invar The Stamina of each Unit must be a valid Stamina for any Unit. 
 * 			| isValidStamina(getStamina())
 * @invar The Name of each Unit must be a valid Name for any Unit. 
 * 			| isValidName(getName())
 * @invar  The Orientation of each Unit must be a valid Orientation for any Unit.
 * *      	| isValidOrientation(getOrientation())
 * @invar The position of each Unit must be a valid position for any Unit.
 *        	| isValidPosition(getPosition())
 * @invar  The remaining attack time of each Unit must be a valid remaining attack time for any
 *         Unit.
 *       	| isValidRemainingAttackTime(getRemainingAttackTime())
 * @invar  The remaining work time of each Unit must be a valid remaining work time for any
 *         Unit.
 *       	| isValidRemainingWorkTime(getRemainingWorkTime())
 * @invar  The remaining attack time of each Unit must be a valid remaining attack time for any
 *         Unit.
 *       	| isValidRemainingAttackTime(getRemainingAttackTime())
 *
 * @author Matthias Fabry and Lukas Van Riel
 * @version 1.0
 *
 */

public class Unit {

	// Constructor //

	/**
	 * Initialize a Unit with given name, initial position, weight, agility, toughness, strength
	 * 		and default behavior
	 * 
	 * @param name
	 *            The Name of the Unit
	 * @param initialPosition
	 *            the initial position of the Unit
	 * @param weight
	 *            the Weight of the Unit
	 * @param agility
	 *            the Agility of the Unit
	 * @param strength
	 *            the Strength of the Unit
	 * @param toughness
	 *            the Toughness of the Unit
	 * @param enableDefaultBehavior
	 *            Flag to signal whether the Unit performs default behavior.
	 * @throws ModelException
	 */
	public Unit(String name, int[] position, int weight, int agility,
			int strength, int toughness, boolean enableDefaultBehavior)
					throws ModelException {
		if (isValidInitialAttribute(agility)) {
			this.setAgility(agility);
		} else
			this.setAgility(nearestValidInitialAttribute(agility));
		if (isValidInitialAttribute(strength)) {
			this.setStrength(strength);
		} else
			this.setStrength(nearestValidInitialAttribute(strength));
		if (isValidInitialAttribute(toughness)) {
			this.setToughness(toughness);
		} else
			this.setToughness(nearestValidInitialAttribute(toughness));
		if (this.isValidInitialWeight(weight)) {
			this.setWeight(weight);
		} else
			this.setWeight(this.nearestValidInitialWeight(weight));
		Coordinate newPosition = new Coordinate(position[0], position[1],
				position[2]).sum(centerCube());
		this.setPosition(newPosition);
		this.setName(name);
		this.setOrientation((float) (Math.PI / 2));
		this.setStamina(this.maxSecondaryAttribute());
		this.setHitpoints(this.maxSecondaryAttribute());
		this.setActivity(Activity.IDLE);
		this.setDefaultBehavior(enableDefaultBehavior);

	}

	// Position (Defensive) //

	/**
	 * Return the position of this Unit.
	 */
	@Basic
	@Raw
	public Coordinate getPosition() {
		return this.position;
	}
	/**
	 * Return the in-world position of this Unit.
	 */
	public Coordinate getInWorldPosition() {
		Coordinate inworldposition = new Coordinate(0, 0, 0);
		inworldposition.setX(this.getPosition().floor().getX());
		inworldposition.setY(this.getPosition().floor().getY());
		inworldposition.setZ(this.getPosition().floor().getZ());;
		return inworldposition;
	}
	/**
	 * Set the position of this Unit to the given position.
	 * 
	 * @param  position
	 *         The new position for this Unit.
	 * @post   The position of this new Unit is equal to
	 *         the given position.
	 *       | new.getPosition() == position
	 * @throws RangeException
	 *         The given position is not a valid position for any
	 *         Unit.
	 *       | ! isValidPosition(getPosition())
	 */
	@Raw
	public void setPosition(Coordinate position) throws ModelException {
		if (!isValidPosition(position))
			throw new ModelException("Invalid Position");
		this.position = new Coordinate(position.getX(), position.getY(),
				position.getZ());
	}
	/**
	 * Check whether the given position is a valid position for
	 * any Unit.
	 *  
	 * @return True if the given position component is valid for this Unit
	 *       | if (position >= MIN_POSITION && result <= MAX_POSITION)
	 *       | 		return True
	 *       | else
	 *       | 		return False
	*/
	public static boolean isValidPosition(Coordinate coordinate) {
		return (coordinate.getX() >= MIN_POSITION
				&& coordinate.getX() <= MAX_POSITION
				&& coordinate.getY() >= MIN_POSITION
				&& coordinate.getY() <= MAX_POSITION
				&& coordinate.getZ() >= MIN_POSITION
				&& coordinate.getZ() <= MAX_POSITION);
	}
	/**
	 * Variable registering the position of this Unit.
	 */
	private Coordinate position;

	/**
	 * Symbolic constant indicating the maximum coordinate in the game world
	 */
	private static final double MAX_POSITION = 50.0;
	/**
	 * Symbolic constant indicating the minimum coordinate in the game world
	 */
	private static final double MIN_POSITION = 0.0;
	/**
	 * Returns a Coordinate object which spans from the lower corner to the center of the cube
	 * @return Coordinate(0.5,0.5,0.5)
	 */
	public static Coordinate centerCube() {
		return new Coordinate(0.5, 0.5, 0.5);
	}

	// Primary Attributes (Total) //

	/**
	 * Checks whether the given attribute is a valid initial value for that attribute
	 * 
	 * @param attribute
	 * 			the attribute to check.
	 * @return Return true when the value lies between 25 and 100, both inclusive
	 * 		| result == (attribute >= 25 && attribute <= 100);
	 */
	public static boolean isValidInitialAttribute(int attribute) {
		return (attribute >= 25 && attribute <= 100);
	}
	/**
	 * Returns the nearest valid initial value of a primary attribute (toughness, strength, agility)
	 * a unit can have
	 * 
	 * @param attribute
	 * 			the value to determine the correct value from
	 * @return Returns 25 if attribute was lower than 25, 100 otherwise
	 * 		| if (attribute < 25)
	 *		| 	then result == 25
	 *		| else
	 *		| 	result == 100
	 */
	public static int nearestValidInitialAttribute(int attribute) {
		if (attribute < 25)
			return 25;
		else
			return 100;
	}
	/**
	 * Check whether the given attribute is a valid value of a primary attribute
	 * (agility, strength, toughness) for any Unit.
	 * 
	 * @param attribute
	 *            The value to check.
	 * @return Returns true if and only if the agility is bigger than or equal to MIN_ATTRIBUTE
	 * 				and smaller than or equal to MAX_ATTRIBUTE
	 * 			| result == attribute <= MAX_ATTRIBUTE && attribute >= MIN_ATTRIBUTE
	 */
	public static boolean isValidAttribute(int attribute) {
		return (attribute <= MAX_ATTRIBUTE && attribute >= MIN_ATTRIBUTE);
	}
	/**
	 * Return the nearest valid value for a primary attribute (agility, strength, toughness)
	 * of a Unit.
	 * 
	 * @param attribute
	 *            The value of which the nearest should be determined
	 * @return If attribute is higher than the highest allowed value for primary attributes, the
	 *         result is that highest allowed value, otherwise, the result is
	 *         the lowest allowed value 
	 *         | if (attribute > MAX_ATTRIBUTE)
	 *         | 	then result == MAX_ATTRIBUTE 
	 *         | else 
	 *         |	result == MIN_ATTRIBUTE
	 */
	public static int nearestValidAttribute(int attribute) {
		if (attribute > MAX_ATTRIBUTE)
			return MAX_ATTRIBUTE;
		else
			return MIN_ATTRIBUTE;
	}

	/**
	 * Checks whether the given value is a valid initial value for Weight
	 * @param weight
	 * 			The weight to check
	 * @return Returns true if weight lies between the lowest allowed weight and 100
	 * 		| if ( lowestValidWeight() <= weight <= 100)
	 * 		| 	then result == true
	 * 		| else
	 * 		| 	result == false
	 */
	public boolean isValidInitialWeight(int weight) {
		return (weight >= this.lowestValidWeight() && weight <= 100);
	}
	/**
	 * Returns the nearest valid initial value of weight
	 * a unit can have
	 * 
	 * @param weight
	 * 			the value to determine the correct value from
	 * @return Returns the lowest allowed weight if attribute was lower than 25, 100 otherwise
	 * 		| if (weight < 25)
	 *		| 	then result == 25
	 *		| else
	 *		| 	result == 100
	 */
	public int nearestValidInitialWeight(int weight) {
		if (weight < this.lowestValidWeight())
			return this.lowestValidWeight();
		else
			return 100;
	}
	/**
	 * Return the nearest valid Weight of a Unit.
	 * 
	 * @param weight
	 *            The Weight of which the nearest should be determined
	 * @return If weight is higher than the highest allowed Weight, the
	 *         result is that highest allowed Weight, otherwise, the result is
	 *         the lowest allowed Weight 
	 *         | if (weight > MAX_ATTRIBUTE)
	 *         | 	then result == MAX_ATTRIBUTE 
	 *         | else 
	 *         |	result == lowestValidWeight(weight)
	 */
	public int nearestValidWeight(int weight) {
		if (weight > MAX_ATTRIBUTE)
			return MAX_ATTRIBUTE;
		else
			return this.lowestValidWeight();
	}
	/**
	 * Return the lowest valid Weight of a Unit.
	 * 
	 * @param weight
	 *            The lowest possible weight for a Unit.
	 */
	public int lowestValidWeight() {
		return (int) Math.ceil((this.getAgility() + this.getStrength()) / 2.0);
	}

	/**
	 * Return the Weight of this Unit.
	 */
	@Basic
	@Raw
	public int getWeight() {
		return this.weight;
	}
	/**
	 * Check whether the given Weight is a valid Weight for any Unit.
	 * 
	 * @param weight
	 *            The Weight to check.
	 * @return | result == (this.getStrength()+this.getAgility())/2 > weight &&
	 *         weight <= 200)
	 */
	public boolean isValidWeight(int weight) {
		return (weight >= this.lowestValidWeight() && weight <= MAX_ATTRIBUTE);
	}
	/**
	 * Set the Weight of this Unit to the given Weight.
	 * 
	 * @param weight
	 *            The new Weight for this Unit.
	 * @post If the given Weight is a valid Weight for any Unit, the Weight of
	 *       this new Unit is equal to the given Weight. | if
	 *       (isValidWeight(weight)) | then new.getWeight() == weight
	 */
	@Raw
	public void setWeight(int weight) {
		if (isValidWeight(weight))
			this.weight = weight;
		else
			this.weight = this.nearestValidWeight(weight);
	}
	/**
	 * Variable registering the Weight of this Unit.
	 */
	private int weight;

	/**
	 * Return the Agility of this Unit.
	 */
	@Basic
	@Raw
	public int getAgility() {
		return this.agility;
	}
	/**
	 * Set the Agility of this Unit to the given Agility.
	 * 
	 * @param agility
	 *            The new Agility for this Unit.
	 * @post If the given Agility is a valid Agility for any Unit, the Agility
	 *       of this new Unit is equal to the given Agility, otherwise it is set to the
	 *       nearest allowed value
	 *       | if (isValidAgility(agility)) 
	 *       | 		then new.getAgility() == agility
	 *       | else 
	 *       |		new.getAgility() == nearestValidAttribute(agility)
	 */
	@Raw
	public void setAgility(int agility) {
		if (isValidAttribute(agility))
			this.agility = agility;
		else
			this.agility = nearestValidAttribute(agility);
	}
	/**
	 * Variable registering the Agility of this Unit.
	 */
	private int agility;

	/**
	 * Return the Strength of this Unit.
	 */
	@Basic
	@Raw
	public int getStrength() {
		return this.strength;
	}
	/**
	 * Set the Strength of this Unit to the given Strength.
	 * 
	 * @param strength
	 *            The new Strength for this Unit.
	 * @post If the given Strength is a valid Strength for any Unit, the
	 *       Strength of this new Unit is equal to the given Strength.
	 *       Otherwise, the strength will be set to the nearest valid value.
	 *       | if (isValidStrength(strength)) 
	 *       | 		then new.getStrength() == strength 
	 *       | else 
	 *       |		new.getStrength() == nearestValidAttribute(strength)
	 */
	@Raw
	public void setStrength(int strength) {
		if (isValidAttribute(strength))
			this.strength = strength;
		else
			this.strength = nearestValidAttribute(strength);
	}
	/**
	 * Variable registering the Strength of this Unit.
	 */
	private int strength;

	/**
	 * Return the Toughness of this Unit.
	 */
	@Basic
	@Raw
	public int getToughness() {
		return this.toughness;
	}
	/**
	 * Set the Toughness of this Unit to the given Toughness.
	 * 
	 * @param toughness
	 *            The new Toughness for this Unit.
	 * @post If the given Toughness is a valid Toughness for any Unit, the
	 *       Toughness of this new Unit is equal to the given Toughness. 
	 *       | if (isValidToughness(toughness)) 
	 *       | 	then new.getToughness() == toughness
	 *       | else 
	 *       |		new.getToughness() == nearestValidAttribute(toughness)
	 */
	@Raw
	public void setToughness(int toughness) {
		if (isValidAttribute(toughness))
			this.toughness = toughness;
		else
			this.toughness = nearestValidAttribute(toughness);
	}
	/**
	 * Variable registering the Toughness of this Unit.
	 */
	private int toughness;

	/**
	 * Symbolic constant registering the maximum value of primary attributes
	 */
	private static final int MAX_ATTRIBUTE = 200;
	/**
	 * Symbolic constant registering the minimum value of primary attributes
	 */
	private static final int MIN_ATTRIBUTE = 1;

	// Secondary Attributes (Nominal) //

	/**
	 * Returns the maximum value for any secondary attribute of a unit
	 * @return Math.ceil(this.getWeight() * this.getToughness() / 50.0)
	 */
	public int maxSecondaryAttribute() {
		return (int) Math.ceil(this.getWeight() * this.getToughness() / 50.0);
	}
	/**
	 * Check whether the given attribute is a valid Secondary Attribute
	 * (Hitpoints or Stamina) for any Unit.
	 * 
	 * @param attribute
	 *            The Secondary Attribute to check.
	 * @return Returns true if and only if the attribute is bigger than MIN_SEC_ATTRIBUTE
	 * 					and smaller than the units maxSecondaryAttribute().
	 * 			| result == (attribute >= MIN_SEC_ATTRIBUTE && attribute 
	 * 							<= getStrength()*getToughness()/50)
	 */
	public boolean isValidSecAttribute(double attribute) {
		return attribute >= MIN_SEC_ATTRIBUTE
				&& attribute <= this.maxSecondaryAttribute();
	}

	/**
	 * Return the Hitpoints of this Unit.
	 */
	@Basic
	@Raw
	public double getHitpoints() {
		return this.hitpoints;
	}
	/**
	 * Set the Hitpoints of this Unit to the given Hitpoints.
	 * 
	 * @param hitpoints
	 *            The new Hitpoints for this Unit.
	 * @pre The given Hitpoints must be a valid Hitpoints for any Unit. 
	 * 		| isValidHitpoints(hitpoints)
	 * @post The Hitpoints of this Unit is equal to the given Hitpoints. 
	 * 		| new.getHitpoints() == hitpoints
	 */
	@Raw
	public void setHitpoints(double hitpoints) {
		assert this.isValidSecAttribute(hitpoints);
		this.hitpoints = hitpoints;
	}
	/**
	 * Variable registering the Hitpoints of this Unit.
	 */
	private double hitpoints;

	/**
	 * Return the Stamina of this Unit.
	 */
	@Basic
	@Raw
	public double getStamina() {
		return this.stamina;
	}
	/**
	 * Set the Stamina of this Unit to the given Stamina.
	 * 
	 * @param stamina
	 *            The new Stamina for this Unit.
	 * @pre The given Stamina must be a valid Stamina for any Unit. |
	 *      isValidStamina(stamina)
	 * @post The Stamina of this Unit is equal to the given Stamina. 
	 * 		| new.getStamina() == stamina
	 */
	@Raw
	public void setStamina(double stamina) {
		assert this.isValidSecAttribute(stamina);
		this.stamina = stamina;
	}
	/**
	 * Variable registering the Stamina of this Unit.
	 */
	private double stamina;

	/**
	 * Symbolic constant registering the minimum value of secondary attributes.
	 */
	private static final int MIN_SEC_ATTRIBUTE = 0;

	// Name (defensive) //

	/**
	 * Return the Name of this Unit.
	 */
	@Basic
	@Raw
	public String getName() {
		return this.name;
	}
	/**
	 * Check whether the given Name is a valid Name for any Unit.
	 * 
	 * @param name
	 *            The Name to check.
	 * @return Returns true if and only if all the characters in name 
	 * 			appear in the pattern validCharacters
	 * 			| if (name matches validCharacters)
	 * 			|	result == true
	 * 			| else
	 * 			|	result == false
	 */
	public static boolean isValidName(String name) {
		if (name == null || name.length() == 0)
			return false;
		Matcher fullName = validCharacters.matcher(name);
		boolean fullNameCorrect = fullName.matches();
		Matcher firstLetter = upperCase.matcher(name.substring(0, 1));
		boolean firstLetterCorrect = firstLetter.matches();
		return (fullNameCorrect && firstLetterCorrect && name.length() >= 2);
	}
	/**
	 * Set the Name of this Unit to the given Name.
	 * 
	 * @param name
	 *            The new Name for this Unit.
	 * @post The Name of this new Unit is equal to the given Name. 
	 * 			|new.getName() == name
	 * @throws ModelException
	 *      	The given Name is not a valid Name for any Unit. 
	 *      	| ! isValidName(getName())
	 */
	@Raw
	public void setName(String name) throws ModelException {
		if (!isValidName(name))
			throw new ModelException("Invalid Name");
		this.name = name;
	}
	/**
	 * Variable registering the Name of this Unit.
	 */
	private String name;

	/**
	 * Pattern containing the set of valid characters for the first letter of Unit names.
	 */
	private static final Pattern upperCase = Pattern.compile("[A-Z]");
	/**
	 * Pattern containing the set of valid characters for Unit names.
	 */
	private static final Pattern validCharacters = Pattern
			.compile("[[a-zA-Z][\"][\'][\\s]]*");

	// Orientation (total) //

	/**
	 * Return the Orientation of this Unit.
	 */
	@Basic
	@Raw
	public float getOrientation() {
		return this.orientation;
	}
	/**
	 * Check whether the given Orientation is a valid Orientation for
	 * any Unit.
	 *  
	 * @param  orientation
	 *         The Orientation to check.
	 * @return 
	 *       | result == 
	*/
	public static boolean isValidOrientation(float orientation) {
		return (orientation > (float) -Math.PI
				&& orientation <= (float) Math.PI);
	}
	/**
	 * Set the Orientation of this Unit to the given Orientation.
	 * 
	 * @param  orientation
	 *         The new Orientation for this Unit.
	 * @post   If the given Orientation is a valid Orientation for any Unit,
	 *         the Orientation of this new Unit is equal to the given
	 *         Orientation.
	 *       | if (isValidOrientation(orientation))
	 *       |   then new.getOrientation() == orientation
	 */
	@Raw
	public void setOrientation(float orientation) {
		if (isValidOrientation(orientation))
			this.orientation = orientation;
		else
			this.orientation = (float) (orientation % 2 * Math.PI);
	}
	/**
	 * Variable registering the Orientation of this Unit.
	 */
	private float orientation;

	// Activity variable (Total) //

	/**
	 * Method that sets the current activity of the Unit.
	 * @param 	activity
	 * 			the acivity the Unit is about to execute
	 */
	void setActivity(Activity activity) {
		this.activity = activity;
	}
	/**
	 * Method returns the current activity of the Unit.
	 */
	public Activity getActivity() {
		return this.activity;
	}
	/**
	 * Variable registering the current activity.
	 */
	private Activity activity = Activity.IDLE;

	// Time control (defensive) //

	/**
	 * Method that advances the game time and carries through any activity
	 * the unit might be performing
	 * 
	 * @param deltaT
	 * 			The time to advance the game time with
	 * 
	 * @post The unit will have carried through the activity it was doing
	 */
	public void advanceTime(double deltaT) {
		if (this.getTimeSinceLastRest() >= 180) {
			try {
				this.rest();
			} catch (ModelException e) {
				// unit not yet ready to rest
			}
		}
		try {
			this.doDefaultBehavior();
		} catch (ModelException e2) {
			// nothing to do here
		}
		try {
			this.working(deltaT);
		} catch (ModelException e3) {
			// nothing to do here
		}
		try {
			this.attacking(deltaT);
		} catch (ModelException e4) {
			// nothing to do here
		}
		try {
			this.updatePosition(deltaT);
		} catch (ModelException e5) {
			// nothing to do here
		}
		try {
			this.resting(deltaT);
		} catch (ModelException e6) {
			this.setTimeSinceLastRest(deltaT + this.getTimeSinceLastRest());
		}
	}

	// Moving (defensive) //

	/**
	 * Method that makes the Unit move to the specified adjacent cube.
	 * @param	x
	 * 			the x-component of the cube the Unit will move to.
	 * @param	y
	 * 			the y-component of the cube the Unit will move to.
	 * @param	z
	 * 			the z-component of the cube the Unit will move to.
	 * @throws	ModelException is the unit is already moving (or sprinting)
	 * 		| (this.getActivity() != Activity.SPRINTING)
	 *		|	&& (this.getActivity() != Activity.MOVING)
	 */
	public void moveToAdjacent(int x, int y, int z) throws ModelException {
		if ((this.getActivity() != Activity.SPRINTING)
				&& (this.getActivity() != Activity.MOVING)) {
			this.clearPath();
			this.addToPath(this.getPosition());
		}
		Coordinate target = new Coordinate(x, y, z).sum(centerCube())
				.sum(this.getPath().getLast().floor());
		if (isValidPosition(target)) {
			this.addToPath(target);
			this.setActivity(Activity.MOVING);
		} else
			throw new ModelException("Already Moving");
	}
	/**
	 * Method that makes the Unit move to a specified position.
	 * @param	x
	 * 			the x-component of the cube the Unit will move to.
	 * @param	y
	 * 			the y-component of the cube the Unit will move to.
	 * @param	z
	 * 			the z-component of the cube the Unit will move to.
	 * @throws	ModelException
	 */
	public void moveTo(int x, int y, int z) throws ModelException {
		if ((this.getActivity() != Activity.MOVING)
				&& (this.getActivity() != Activity.SPRINTING)) {
			this.clearPath();
			Coordinate destinationCube = new Coordinate(x, y, z);
			this.setDestination(destinationCube);
			this.addToPath(this.getPosition());
			this.setActivity(Activity.MOVING);
			this.findPath();
		} else
			throw new ModelException("Already Moving");

	}
	/**
	 * Method that seeks the right path for the Unit to move to its destination.
	 *
	 * @throws	ModelException
	 */
	public void findPath() {
		int x = 0;
		int y = 0;
		int z = 0;
		while ((int) this.getDestinationCube().getX() != (int) this.getPath()
				.getLast().floor().getX()
				|| (int) this.getDestinationCube().getY() != (int) this
						.getPath().getLast().floor().getY()
				|| (int) this.getDestinationCube().getZ() != (int) this
						.getPath().getLast().floor().getZ()) {
			if ((int) this.getDestinationCube().getX() > (int) this.getPath()
					.getLast().floor().getX())
				x = 1;
			else if ((int) this.getDestinationCube().getX() < (int) this
					.getPath().getLast().floor().getX())
				x = -1;
			else
				x = 0;
			if ((int) this.getDestinationCube().getY() > (int) this.getPath()
					.getLast().floor().getY())
				y = 1;
			else if ((int) this.getDestinationCube().getY() < (int) this
					.getPath().getLast().floor().getY())
				y = -1;
			else
				y = 0;
			if ((int) this.getDestinationCube().getZ() > (int) this.getPath()
					.getLast().floor().getZ())
				z = 1;
			else if ((int) this.getDestinationCube().getZ() < (int) this
					.getPath().getLast().floor().getZ())
				z = -1;
			else
				z = 0;
			try {
				this.moveToAdjacent(x, y, z);
			} catch (ModelException e) {
				// shouldn't happen
			}
		}
	}

	/**
	 * Method that stops a unit from moving.
	 * 
	 * @post | this.activity == IDLE
	 */
	public void stopMoving() {
		this.setActivity(Activity.IDLE);
	}
	/**
	 * Method that initiates a unit to sprint.
	 * 
	 * @post | this.activity == SPRINTING
	 */
	public void startSprinting() {
		if (this.canSprint())
			this.setActivity(Activity.SPRINTING);
	}
	/**
	 * Method that indicates whether a Unit is able to sprint
	 * 
	 * return true is and only if the Unit is moving and has stamina that is greater than 0
	 * 	|if this.getActivity() == Activity.MOVING && this.getStamina() > 0
	 * 	| 	return true
	 *	|else
	 *	|	return false
	 */
	public boolean canSprint() {
		return (this.getActivity() == Activity.MOVING && this.getStamina() > 0);
	}
	/**
	 * Method that stops a unit from sprinting.
	 * 
	 * @post | this.setActivity(Activity.MOVING)
	 */
	public void stopSprinting() {
		this.setActivity(Activity.MOVING);
	}

	/**
	 * Method that computes the Unit's walking speed.
	 */
	public double walkingSpeed(int z) {
		double walkingSpeed = 0;
		double baseSpeed = 1.5 * (this.getAgility() + this.getStrength())
				/ (2 * this.getWeight());
		if (z - (int) this.getPath().get(0).getZ() == -1)
			walkingSpeed = 1.2 * baseSpeed;
		else if (z - (int) this.getPath().get(0).getZ() == 1)
			walkingSpeed = 0.5 * baseSpeed;
		else
			walkingSpeed = baseSpeed;
		return walkingSpeed;
	}
	/**
	 * Method that returns the current speed.
	 */
	public double getCurrentSpeed() {
		if (this.getActivity() != Activity.SPRINTING
				&& this.getActivity() != Activity.MOVING)
			return 0;
		int targetZ = (int) Math.floor(this.getPath().get(1).getZ());
		if (this.getActivity() == Activity.MOVING)
			return this.walkingSpeed(targetZ);
		else if (this.getActivity() == Activity.SPRINTING)
			return 2 * this.walkingSpeed(targetZ);
		else
			return 0;

	}

	/**
	 * Method that updates the current position of the Unit.
	 * @param DeltaT
	 * 			the time-interval used in advanceTime()
	 * @throws ModelException
	*/
	public void updatePosition(double deltaT) throws ModelException {
		if (this.getActivity() == Activity.MOVING
				|| this.activity == Activity.SPRINTING) {
			if (this.getPath().size() >= 2) {
				Coordinate start = this.getPath().get(0);
				Coordinate target = this.getPath().get(1);
				Coordinate direction = start.directionVector(target);
				Coordinate displacement = direction
						.scalarMult(this.getCurrentSpeed() * deltaT);
				if (displacement.length() >= this.remaininglegDistance()) {
					try {
						this.setPosition(target);
					} catch (ModelException e) {
						// shouldn't happen
					}
					this.getPath().remove(0);
					if (this.getPath().size() < 2)
						this.stopMoving();
				} else {
					try {
						this.setPosition(this.getPosition().sum(displacement));
					} catch (ModelException e) {
						// shoudn't happen
					}
					this.setOrientation((float) Math.atan2(direction.getY(),
							direction.getX()));
					if (this.getActivity() == Activity.SPRINTING) {
						if (this.getStamina() - deltaT / 0.1 > 0)
							this.setStamina(this.getStamina() - deltaT / 0.1);
						else {
							this.setStamina(MIN_SEC_ATTRIBUTE);
							this.stopSprinting();
						}
					}
				}
			} else
				this.stopMoving();
		} else
			throw new ModelException("Unit is not in a moving state");
	}
	/**
	 * Method that returns the remaining distance to reach the first target cube in the path.	
	 */
	private double remaininglegDistance() {
		Coordinate vector = this.getPath().get(1)
				.difference(this.getPosition());
		return vector.length();
	}
	/**
	 * Method that returns adds a next destination to the path.
	 * @param 	target
	 * 			the coordinate that needs to be added to the path.
	 */
	public void addToPath(Coordinate target) {
		this.getPath().addLast(target);
	}
	/**
	 * Method that clears the Unit's path.
	 */
	public void clearPath() {
		this.getPath().clear();
	}
	/**
	 * Method that sets the cube the Unit will move to.
	 * @param	destinationCube
	 * 			the cube the Unit will move to.
	 */
	public void setDestination(Coordinate destinationCube) {
		this.destinationCube = destinationCube;
	}
	/**
	 * Method that returns the cube the Unit is moving to.
	 */
	public Coordinate getDestinationCube() {
		return this.destinationCube;
	}
	/**
	 * Coordinate that keeps the cube the Unit is moving to.
	 */
	private Coordinate destinationCube;
	/**
	 * Method that returns the current Path.
	 */
	public LinkedList<Coordinate> getPath() {
		return this.path;
	}
	/**
	 * Linked list that keeps the Path the Unit is about to walk.
	 */
	private LinkedList<Coordinate> path = new LinkedList<>();

	// Working (defensive) //

	/**
	 * Method that initiates a work task for a Unit.
	 * 
	 * @post | this.getActivity() == WORKING
	 * @post | this.getRemainingWorkTime == this.workTime()
	 * @throws ModelException
	 */
	public void work() throws ModelException {
		if (this.getActivity() != Activity.MOVING
				&& this.getActivity() != Activity.SPRINTING
				&& this.getActivity() != Activity.WORKING
				&& this.getActivity() != Activity.ATTACKING
				&& this.getActivity() != Activity.DEFENDING) {
			try {
				this.setRemainingWorkTime(this.workTime());
			} catch (ModelException exc) {
				// shouldn't happen
			}
			this.setActivity(Activity.WORKING);
		} else
			throw new ModelException("Unit not ready to work");
	}
	/**
	 * Method that simulates the working behavior of a Unit
	 * @param deltaT
	 * 			The time to work for
	 * @throws ModelException
	 * 			When the unit isn't in the working state
	 * @post   The remaining work time is lowered by deltaT, 
	 * 			if able, otherwise, it is set to 0
	 * 		| new.getRemainingWorkTime == this.getRemainingWorkTime - deltaT
	 */
	public void working(double deltaT) throws ModelException {
		if (this.getActivity() != Activity.WORKING)
			throw new ModelException("The unit isn't in a working state");
		else
			try {
				this.setRemainingWorkTime(this.getRemainingWorkTime() - deltaT);
			} catch (ModelException e) {
				this.setRemainingWorkTime(0);
			}
	}
	/**
	 * Method that stops a work task for a Unit
	 * 
	 * @post | this.setActivity(Activity.IDLE)
	 */
	public void stopWork() {
		this.setActivity(Activity.IDLE);
	}

	/**
	 * Return the remaining work time of this Unit.
	 */
	@Basic
	@Raw
	public double getRemainingWorkTime() {
		return this.remainingWorkTime;
	}
	/**
	 * Check whether the given remaining work time is a valid remaining work time for
	 * any Unit.
	 *  
	 * @param  remaining work time
	 *         The remaining work time to check.
	 * @return 
	 *       | result == remainingWorkTime < this.workTime()
	*/
	public boolean isValidRemainingWorkTime(double remainingWorkTime) {
		return (remainingWorkTime <= this.workTime() && remainingWorkTime >= 0);
	}
	/**
	 * Set the remaining work time of this Unit to the given remaining work time.
	 * 
	 * @param  remainingWorkTime
	 *         The new remaining work time for this Unit.
	 * @post   The remaining work time of this new Unit is equal to
	 *         the given remaining work time.
	 *       | new.getRemainingWorkTime() == remainingWorkTime
	 * @throws ModelException
	 *         The given remaining work time is not a valid remaining work time for any
	 *         Unit.
	 *       | ! isValidRemainingWorkTime(getRemainingWorkTime())
	 */
	@Raw
	public void setRemainingWorkTime(double remainingWorkTime)
			throws ModelException {
		if (!isValidRemainingWorkTime(remainingWorkTime))
			throw new ModelException();
		this.remainingWorkTime = remainingWorkTime;
	}

	/**
	 * Method that computes how long it takes for a unit to finish a work task.
	 * @return 500.0 / this.getStrength()
	 */
	public double workTime() {
		return (500.0d / this.getStrength());
	}
	/**
	 * Variable registering the remaining work time of this Unit.
	 */
	private double remainingWorkTime;

	// Fighting (defensive) //

	/**
	 * Method that initiates an attack on another Unit
	 * @param victim
	 * 			The Unit to attack
	 * @throws ModelException
	 */
	public void attack(Unit victim) throws ModelException {
		this.setVictim(victim);
		Coordinate attackVector = this.getVictim().getPosition()
				.difference(this.getPosition());
		if (attackVector.length() <= Math.sqrt(3)) {
			this.setRemainingAttackTime(attackTime);
			this.setActivity(Activity.ATTACKING);
			this.getVictim().setActivity(Activity.DEFENDING);
			this.orientWith(this.getVictim());
		} else
			throw new ModelException("target too far away");
	}
	public void attacking(double DeltaT) throws ModelException {
		if (this.getActivity() == Activity.ATTACKING) {
			try {
				this.setRemainingAttackTime(
						this.getRemainingAttackTime() - DeltaT);
			} catch (ModelException exc) {
				try {
					this.setRemainingAttackTime(0);
				} catch (ModelException ex) {
					// shouldn't happen
				}
				this.stopAttack();
			}
		} else
			throw new ModelException("This unit isn't in an attacking state");
	}
	/**
	 * Method that stops an attack of a Unit, inflicting damage 
	 * when the victim doesn't successfully defend
	 * @throws ModelException 
	 */
	public void stopAttack() {
		if (!this.getVictim().defend(this))
			this.doesDamage(this.getVictim());
		this.setActivity(Activity.IDLE);
		victim.setActivity(Activity.IDLE);
		try {
			this.setVictim(null);
		} catch (ModelException e) {
			// shouldn't happen
		}
	}

	/**
	 * Method that gets the attacked Unit.
	 */
	public Unit getVictim() {
		return this.victim;
	}
	/**
	 * Method that determines whether the victim is a valid victim for a unit
	 * @param victim
	 * 			the victim to consider
	 * @return returns true: in this iteration of the game, any unit (including null) 
	 * 			is a valid victim.
	 * 
	 *		| result == true
	 */
	public boolean isValidVictim(Unit victim) {
		return true;
	}
	/**
	 * Set the unit's victim to the given victim
	 * 
	 * @param victim
	 * 			the new victim for this unit
	 * @post	the victim of the new unit is the given victim
	 * 		| new.getVictim() == victim
	 * @throws ModelException
	 * 			the given victim isn't a valid victim
	 * 		| ! isValidVictim()
	 */
	public void setVictim(Unit victim) throws ModelException {
		if (isValidVictim(victim))
			this.victim = victim;
		else
			throw new ModelException("Not a valid victim");
	}
	/**
	 * Variable registering the Unit that is being attacked.
	 */
	private Unit victim = null;

	/**
	 * Return the remaining attack time of this Unit.
	 */
	@Basic
	@Raw
	public double getRemainingAttackTime() {
		return this.remainingAttackTime;
	}
	/**
	 * Check whether the given remaining attack time is a valid remaining attack time for
	 * any Unit.
	 *  
	 * @param  remaining attack time
	 *         The remaining attack time to check.
	 * @return 
	 *       | result == 
	*/
	public static boolean isValidRemainingAttackTime(
			double remainingAttackTime) {
		return (remainingAttackTime >= 0 && remainingAttackTime <= attackTime);
	}
	/**
	 * Set the remaining attack time of this Unit to the given remaining attack time.
	 * 
	 * @param  remainingAttackTime
	 *         The new remaining attack time for this Unit.
	 * @post   The remaining attack time of this new Unit is equal to
	 *         the given remaining attack time.
	 *       | new.getRemainingAttackTime() == remainingAttackTime
	 * @throws ModelException
	 *         The given remaining attack time is not a valid remaining attack time for any
	 *         Unit.
	 *       | ! isValidRemainingAttackTime(getRemainingAttackTime())
	 */
	@Raw
	public void setRemainingAttackTime(double remainingAttackTime)
			throws ModelException {
		if (!isValidRemainingAttackTime(remainingAttackTime))
			throw new ModelException();
		this.remainingAttackTime = remainingAttackTime;
	}
	/**
	 * Variable registering the remaining attack time of this Unit.
	 */
	private double remainingAttackTime;

	/**
	 * Method that simulates the defending behavior of a Unit
	 *  
	 * @param attacker
	 * 			The Unit that attacks this Unit
	 * @return Returns true when the Unit defends successfully, false when otherwise
	 * 			| if (! this.dodge(attacker))
	 *		    | 	then if (!this.block(attacker))
	 *			|  		then return false;
	 *			| return true;
	 * @throws ModelException
	 */
	public boolean defend(Unit attacker) {
		if (!this.dodge(attacker)) {
			if (!this.block(attacker)) {
				return false;
			}
			return true;
		}
		return true;
	}
	/**
	 * Method that simulates the dodge behavior of a Unit.
	 * 
	 * @param attacker
	 * 			The Unit that attacks this Unit
	 * @return Returns true when the Unit dodges successfully, and updates the Unit's position
	 * 			to a random position neighboring the unit's current position. Returns false when
	 * 			when the dodge is unsuccessful.
	 * 			| if (random < chance)
	 * 			| then new.getPostion() = this.getPosition() + random jump
	 * 			|  			&& return true
	 * 			| else
	 * 			|	return false
	 * @throws ModelException
	 */
	public boolean dodge(Unit attacker) {
		double chance = 0.20
				* ((double) this.getAgility() / attacker.getAgility());
		double random = Math.random();
		if (random <= chance) {
			int x = 0;
			int y = 0;
			int z = 0;
			Coordinate target = this.getInWorldPosition().sum(centerCube());
			while ((x == 0 && y == 0 && z == 0) || !isValidPosition(target)) {
				Random posDecider = new Random();
				x = (posDecider.nextInt(3)) - 1;
				y = (posDecider.nextInt(3)) - 1;
				z = (posDecider.nextInt(3)) - 1;
				target = new Coordinate(x, y, z);
				target = target
						.sum(this.getInWorldPosition().sum(centerCube()));
			}
			try {
				this.setPosition(target);
			} catch (ModelException exc) {
				// should never happen
			}
			return true;
		}
		return false;
	}
	/**
	 * Method that simulates the blocking behavior of a Unit.
	 * 
	 * @param attacker
	 * 			The Unit that attacks this Unit
	 * @return Returns true when the Unit blocks successfully. Returns false when
	 * 			when the dodge is unsuccessful.
	 * 			| if (random < chance)
	 * 			|  	then return true
	 * 			| else
	 * 			|	return false
	 * @throws ModelException
	 */
	public boolean block(Unit attacker) {
		double chance = 0.25
				* ((double) (this.getStrength() + this.getAgility())
						/ (attacker.getAgility() + attacker.getStrength()));
		double random = Math.random();
		return (random < chance);
	}

	/**
	 * Changes the hitpoints of the victim due to an attack
	 * 
	 * @param victim
	 * 			The Unit to which damage is dealt.
	 * @post The victim's hitpoints are lowered with the attacker's strength / 10 
	 * 		| new.victim.getHitpoints = victim.getHitpoints - this.getStrength() / 10
	 */
	public void doesDamage(Unit victim) {
		victim.setHitpoints(victim.getHitpoints() - this.getStrength() / 10);
	}

	/**
	 * Method that orients the attacking unit with the defending unit
	 * @param defender
	 * 			The unit that this unit is attacking
	 * @post the Units will face each other
	 */
	public void orientWith(Unit defender) {
		this.setOrientation(
				(float) Math.atan2(
						defender.getPosition().getY() - this.getPosition()
								.getY(),
				(defender.getPosition().getX() - this.getPosition().getX())));
		defender.setOrientation(
				(float) Math.atan2(
						this.getPosition().getY() - defender.getPosition()
								.getY(),
				(this.getPosition().getX() - defender.getPosition().getX())));
	}

	/**
	 * Symbolic constant that contains the time it takes to conduct an attack
	 */
	public static double attackTime = 1.0;

	// Resting (defensive) //

	/**
	 * Method that initiates the unit is resting.
	 * @throws ModelException 
	 * 
	 * @post | new.getActivity == Activity.RESTING
	 */
	public void rest() throws ModelException {
		if (this.getActivity() != Activity.DEFENDING
				&& this.getActivity() != Activity.ATTACKING
				&& this.getActivity() != Activity.MOVING
				&& this.getActivity() != Activity.SPRINTING) {
			this.setActivity(Activity.RESTING);
			this.setTimeResting(0.0);
		} else
			throw new ModelException("Unit not ready to rest");
	}
	/**
	 * Method that governs the resting process
	 * @param DeltaT
	 * 			the time-interval used in advanceTime()
	 * @throws ModelException
	*/
	public void resting(double DeltaT) throws ModelException {
		if ((this.getActivity() != Activity.RESTING))
			throw new ModelException("Unit isn't in a resting state");
		this.setTimeSinceLastRest(0.0);
		double timeBefore = this.getTimeResting();
		this.setTimeResting(this.getTimeResting() + DeltaT);
		double timeAfter = this.getTimeResting();

		if (this.getHitpoints() + (this.getToughness() / 40) * DeltaT < this
				.maxSecondaryAttribute()) {
			if (timeBefore < this.timeToRecoverOneHP()
					&& timeAfter >= this.timeToRecoverOneHP())
				this.setHitpoints(this.getHitpoints() + 1);
			else if (this.getTimeResting() >= this.timeToRecoverOneHP()) {
				this.setHitpoints((this.getHitpoints()
						+ (this.getToughness() / 40 * DeltaT)));
			}
		} else if (this.getStamina()
				+ (this.getToughness() / 20) * DeltaT < this
						.maxSecondaryAttribute()) {
			this.setHitpoints(this.maxSecondaryAttribute());
			if (timeBefore < this.timeToRecoverOneStamina()
					&& timeAfter >= this.timeToRecoverOneStamina())
				this.setStamina(this.getStamina() + 1);
			else if (this.getTimeResting() >= this.timeToRecoverOneStamina()) {
				this.setStamina((this.getStamina()
						+ (this.getToughness() / 20) * DeltaT));
			}
		} else {
			this.setStamina(this.maxSecondaryAttribute());
			this.stopResting();
		}
	}
	/**
	 * Method that stops a unit from resting.
	 * 
	 * @post | this.setActivity(Activity.IDLE)
	 */
	public void stopResting() {
		this.setTimeSinceLastRest(0);
		this.setActivity(Activity.IDLE);
	}
	/**
	 * Method that gets the time the Unit needs to rest in order to gain 1 Hit-point.
	 */
	public double timeToRecoverOneHP() {
		return 0.2 * (200.0 / this.getToughness());
	}
	/**
	 * Method that gets the time the Unit needs to rest in order to gain 1 Stamina-point.
	 */
	public double timeToRecoverOneStamina() {
		return 0.2 * (100.0 / this.getToughness());
	}
	/**
	 * Method that gets the time the Unit needs to rest.
	 */
	public double getTimeResting() {
		return timeResting;
	}
	/**
	 * Method that sets the time the Unit is resting.
	 */
	public void setTimeResting(double timeResting) {
		this.timeResting = timeResting;
	}
	/**
	 * Variable registering the time a Unit is resting.
	 */
	private double timeResting = 0.0;
	/**
	 * Method that counts the time since the Unit last had a rest.
	 * 
	 */
	public double getTimeSinceLastRest() {
		return timeSinceLastRest;
	}
	/**
	 * Method that sets the time since the Unit last had a rest.
	 * 
	 */
	public void setTimeSinceLastRest(double timeSinceLastRest) {
		this.timeSinceLastRest = timeSinceLastRest;
	}
	/**
	 * Variable registering the time since the Unit last had a rest.
	 */
	private double timeSinceLastRest = 0;

	// Default behavior (defensive) //

	/**
	 * Method that initiates the unit's default behavior.
	 * 
	 * @throws ModelException when the unit isn't Idle
	 * 		 | this.getActivity != Idle
	 * 
	 * @post | new.activeDefaultBehaviour == true
	 */
	public void startDefaultBehavior() throws ModelException {
		if (this.getActivity() == Activity.IDLE) {
			this.setDefaultBehavior(true);
		} else
			throw new ModelException("Unit isn't Idle!");
	}
	/**
	 * Method that governs a Unit during default behavior
	 * @param deltaT
	 * 			the time-interval used in advanceTime()
	 * @throws ModelException
	*/
	public void doDefaultBehavior() throws ModelException {
		if (!this.getDefaultBehavior())
			throw new ModelException("Unit isn't executing default behavior");
		if (this.getActivity() == Activity.IDLE) {
			Random random = new Random();
			int decider = random.nextInt(3);
			if (this.getDefaultBehavior()) {
				if (decider == 0) {
					rest();
				} else if (decider == 1) {
					work();
				} else {
					int x = random.nextInt(51);
					int y = random.nextInt(51);
					int z = random.nextInt(51);
					try {
						moveTo(x, y, z);
					} catch (Exception e) {
						// shouldn't happen
					}
				}
			}
		}
	}
	/**
	 * Method that stops a Unit from executing default behaviour.
	*/
	public void stopDefaultBehavior() {
		this.setDefaultBehavior(false);
	}
	/**
	 * Method that makes a Unit execute default behaviour.
	*/
	public void setDefaultBehavior(boolean flag) {
		this.defaultBehavior = flag;
	}
	public boolean getDefaultBehavior() {
		return this.defaultBehavior;
	}
	/**
	 * flag registering whether a Unit is executing defaultbehaviour.
	*/
	private boolean defaultBehavior = false;

}
