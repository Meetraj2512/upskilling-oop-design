import java.util.List;

class LinkedListNode {
    int data;
    LinkedListNode next;

    public LinkedListNode(int data, LinkedListNode next) {
        this.data = data;
        this.next = next;
    }

    public LinkedListNode(int data) {
        this.data = data;
    }
}

public class LL {
    public LinkedListNode buildLinkedList(List<Integer> nums) {
        if (nums == null || nums.isEmpty()) {
            return null;
        }
        LinkedListNode head = new LinkedListNode(nums.get(0));
        LinkedListNode current = head;
        for (int i = 1; i < nums.size(); i++) {
            current.next = new LinkedListNode(nums.get(i));
            current = current.next;
        }
        return head;
    }

    public void printLinkedlist(LinkedListNode head) {
        if (head == null) {
            return;
        }
        LinkedListNode temp = head;
        int nodeCounter = 1;
        while (temp != null) {
            System.out.println("Data at node " + nodeCounter + " is:" + temp.data);
            nodeCounter++;
            temp = temp.next;
        }
    }

    public LinkedListNode removeHead(LinkedListNode head) {
        if (head == null)
            return null;
        return head.next;
    }

    public LinkedListNode removeTail(LinkedListNode head) {
        if (head == null || head.next == null)
            return null;
        LinkedListNode temp = head;
        while (temp.next.next != null) {
            temp = temp.next;
        }
        temp.next = null;
        return head;
    }

    public LinkedListNode removeNode(LinkedListNode head, int data) {
        if (head == null)
            return null;
        if (head.data == data) {
            return head.next;
        }
        LinkedListNode temp = head;
        while (temp.next != null) {
            if (temp.next.data == data) {
                temp.next = temp.next.next;
                return head;
            }
            temp = temp.next;
        }
        return head;
    }

    public static void main(String[] args) {
        LL ll = new LL();
        List<Integer> a = List.of(1, 2, 3, 4, 5);
        LinkedListNode head = ll.buildLinkedList(a);
        ll.printLinkedlist(head);
        LinkedListNode headRemoved = ll.removeHead(head);
        System.out.println("After removing head");
        ll.printLinkedlist(headRemoved);
        LinkedListNode tailRemoved = ll.removeTail(headRemoved);
        System.out.println("After removing tail");
        ll.printLinkedlist(tailRemoved);
        LinkedListNode nodeRemoved = ll.removeNode(tailRemoved, 3);
        System.out.println("After removing node");
        ll.printLinkedlist(nodeRemoved);
    }
}
