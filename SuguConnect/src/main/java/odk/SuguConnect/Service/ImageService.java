package odk.SuguConnect.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class ImageService {
    
    /**
     * Redimensionne une image pour créer une vignette avec un meilleur ratio
     * @param originalImage L'image originale
     * @param targetWidth Largeur cible
     * @param targetHeight Hauteur cible
     * @return Image redimensionnée
     * @throws IOException Si une erreur se produit lors du traitement de l'image
     */
    public BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
        // Calculer les dimensions pour conserver les proportions
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        
        // Calculer le facteur d'échelle pour remplir entièrement la zone cible
        double scale = Math.max((double) targetWidth / originalWidth, (double) targetHeight / originalHeight);
        
        // Calculer les nouvelles dimensions
        int scaledWidth = (int) (originalWidth * scale);
        int scaledHeight = (int) (originalHeight * scale);
        
        // Redimensionner l'image
        Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        
        // Créer une image tampon pour le recadrage
        BufferedImage scaledBufferedImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = scaledBufferedImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();
        
        // Calculer les coordonnées pour centrer le recadrage
        int x = (scaledWidth - targetWidth) / 2;
        int y = (scaledHeight - targetHeight) / 2;
        
        // Créer l'image finale recadrée
        BufferedImage finalImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D finalGraphics = finalImage.createGraphics();
        
        // Remplir le fond en blanc
        finalGraphics.setColor(Color.WHITE);
        finalGraphics.fillRect(0, 0, targetWidth, targetHeight);
        
        // Dessiner l'image recadrée
        finalGraphics.drawImage(scaledBufferedImage, 0, 0, targetWidth, targetHeight, x, y, x + targetWidth, y + targetHeight, null);
        finalGraphics.dispose();
        
        return finalImage;
    }
    
    /**
     * Crée une vignette à partir d'un fichier image
     * @param file Fichier image
     * @param width Largeur de la vignette
     * @param height Hauteur de la vignette
     * @return Tableau de bytes de la vignette
     * @throws IOException Si une erreur se produit lors du traitement de l'image
     */
    public byte[] createThumbnail(MultipartFile file, int width, int height) throws IOException {
        BufferedImage originalImage = convertMultipartFileToBufferedImage(file);
        BufferedImage thumbnail = resizeImage(originalImage, width, height);
        return convertBufferedImageToBytes(thumbnail, getFileExtension(file.getOriginalFilename()));
    }
    
    /**
     * Convertit un MultipartFile en BufferedImage
     * @param file Fichier multipart
     * @return BufferedImage
     * @throws IOException Si une erreur se produit lors de la conversion
     */
    private BufferedImage convertMultipartFileToBufferedImage(MultipartFile file) throws IOException {
        return ImageIO.read(file.getInputStream());
    }
    
    /**
     * Convertit un BufferedImage en tableau de bytes
     * @param image Image à convertir
     * @param format Format de l'image (jpg, png, etc.)
     * @return Tableau de bytes
     * @throws IOException Si une erreur se produit lors de la conversion
     */
    private byte[] convertBufferedImageToBytes(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Améliorer la qualité JPEG si applicable
        if ("jpg".equalsIgnoreCase(format) || "jpeg".equalsIgnoreCase(format)) {
            ImageIO.write(image, "jpg", baos);
        } else {
            ImageIO.write(image, format, baos);
        }
        return baos.toByteArray();
    }
    
    /**
     * Récupère l'extension d'un fichier
     * @param fileName Nom du fichier
     * @return Extension du fichier
     */
    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return "jpg"; // Par défaut
    }
}