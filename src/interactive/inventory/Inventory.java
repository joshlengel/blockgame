package interactive.inventory;

public class Inventory {

	public static final int SLOTS = 8;
	
	private int selectedSlot;
	private Stack[] slots;
	
	public class Stack {
		String item;
		int amount;
	}
	
	public Inventory() {
		slots = new Stack[SLOTS];
	}
	
	public Inventory(Stack[] slots, int selectedSlot) {
		this.slots = slots;
		this.selectedSlot = selectedSlot;
	}
	
	public boolean available() {
		return slots[selectedSlot] != null;
	}
	
	public String removeItem() {
		slots[selectedSlot].amount--;
		
		String item = slots[selectedSlot].item;
		
		if (slots[selectedSlot].amount == 0)
			slots[selectedSlot] = null;
		
		return item;
	}
	
	public void addItem(String item) {
		for (Stack s : slots) {
			if (s != null && s.item.contentEquals(item) && s.amount < 64) {
				s.amount++;
				return;
			}
		}
		
		for (int i = 0; i < SLOTS; i++) {
			if (slots[i] == null) {
				slots[i] = new Stack();
				slots[i].item = item;
				slots[i].amount = 1;
				return;
			}
		}
	}
	
	public void increaseSlot() {
		if (selectedSlot < SLOTS - 1)
			selectedSlot++;
	}
	
	public void decreaseSlot() {
		if (selectedSlot > 0)
			selectedSlot--;
	}
	
	public void setSlot(int i) {
		selectedSlot = i;
	}
	
	public int getSelectedSlot() {
		return selectedSlot;
	}
	
	public boolean available(int slot) {
		return slots[slot] != null;
	}
	
	public String getItem(int slot) {
		return slots[slot].item;
	}
	
	public int getItems(int slot) {
		return slots[slot].amount;
	}
}