import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.ArrayList;

public class ImageSelection implements Transferable {
    private static ArrayList<DataFlavor> imageFlavors = new ArrayList<DataFlavor>();

    static {
        try {
            imageFlavors.add(new DataFlavor("image/x-java-image; class=java.awt.Image"));
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private Image image;

    public ImageSelection(Image image) {
        this.image = image;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return (DataFlavor[]) imageFlavors.toArray(new DataFlavor[imageFlavors.size()]);
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return imageFlavors.contains(flavor);
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (Image.class.equals(flavor.getRepresentationClass())) {
            if(flavor.getHumanPresentableName().equals("image/x-java-image"))
                return image;
            else
                return null;
        }
        else
            return null;
    }
}
