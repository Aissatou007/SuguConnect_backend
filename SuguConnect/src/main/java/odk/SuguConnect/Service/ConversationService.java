package odk.SuguConnect.Service;

import odk.SuguConnect.Entity.Conversation;
import odk.SuguConnect.Entity.Consommateur;
import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Entity.Produit;
import odk.SuguConnect.Repository.ConversationRepository;
import odk.SuguConnect.Repository.ConsommateurRepository;
import odk.SuguConnect.Repository.ProducteurRepository;
import odk.SuguConnect.Repository.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ConversationService {
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    @Autowired
    private ConsommateurRepository consommateurRepository;
    
    @Autowired
    private ProducteurRepository producteurRepository;
    
    @Autowired
    private ProduitRepository produitRepository;
    
    public Conversation creerConversation(Long consommateurId, Long producteurId, Long produitId) {
        // Vérifier si une conversation existe déjà entre ces participants pour ce produit
        Optional<Conversation> conversationExistante = 
            conversationRepository.findByConsommateurIdAndProducteurIdAndProduitId(
                consommateurId, producteurId, produitId);
        
        if (conversationExistante.isPresent()) {
            return conversationExistante.get();
        }
        
        // Créer une nouvelle conversation
        Conversation conversation = new Conversation();
        
        // Récupérer les entités associées
        Optional<Consommateur> consommateur = consommateurRepository.findById(consommateurId.intValue());
        Optional<Producteur> producteur = producteurRepository.findById(producteurId.intValue());
        
        if (consommateur.isPresent() && producteur.isPresent()) {
            conversation.setConsommateur(consommateur.get());
            conversation.setProducteur(producteur.get());
            
            // Si un produit est spécifié, le récupérer
            if (produitId != null) {
                Optional<Produit> produit = produitRepository.findById(produitId.intValue());
                if (produit.isPresent()) {
                    conversation.setProduit(produit.get());
                }
            }
            
            conversation.setDateCreation(LocalDateTime.now());
            conversation.setDateDernierMessage(LocalDateTime.now());
            conversation.setActive(true);
            
            return conversationRepository.save(conversation);
        }
        
        return null;
    }
    
    public Optional<Conversation> getConversationById(Long id) {
        return conversationRepository.findById(id);
    }
    
    public List<Conversation> getConversationsByConsommateur(Long consommateurId) {
        return conversationRepository.findByConsommateurIdAndActiveTrue(consommateurId);
    }
    
    public List<Conversation> getConversationsByProducteur(Long producteurId) {
        return conversationRepository.findByProducteurIdAndActiveTrue(producteurId);
    }
    
    public void desactiverConversation(Long conversationId) {
        Optional<Conversation> conversationOpt = conversationRepository.findById(conversationId);
        if (conversationOpt.isPresent()) {
            Conversation conversation = conversationOpt.get();
            conversation.setActive(false);
            conversationRepository.save(conversation);
        }
    }
}