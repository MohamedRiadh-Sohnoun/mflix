package mflix.api.daos;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoWriteException;
import com.mongodb.ReadConcern;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Accumulators.sum;
import com.mongodb.client.model.Aggregates;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.limit;
import static com.mongodb.client.model.Aggregates.sort;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import static com.mongodb.client.model.Sorts.descending;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import mflix.api.models.Comment;
import mflix.api.models.Critic;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Component
public class CommentDao extends AbstractMFlixDao {

    public static String COMMENT_COLLECTION = "comments";
    private final Logger log;
    private MongoCollection<Comment> commentCollection;
    private CodecRegistry pojoCodecRegistry;

    @Autowired
    public CommentDao(
            MongoClient mongoClient, @Value("${spring.mongodb.database}") String databaseName) {
        super(mongoClient, databaseName);
        log = LoggerFactory.getLogger(this.getClass());
        this.db = this.mongoClient.getDatabase(MFLIX_DATABASE);
        this.pojoCodecRegistry
                = fromRegistries(
                        MongoClientSettings.getDefaultCodecRegistry(),
                        fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        this.commentCollection
                = db.getCollection(COMMENT_COLLECTION, Comment.class).withCodecRegistry(pojoCodecRegistry);
    }

    /**
     * Returns a Comment object that matches the provided id string.
     *
     * @param id - comment identifier
     * @return Comment object corresponding to the identifier value
     */
    public Comment getComment(String id) {
        return commentCollection.find(new Document("_id", new ObjectId(id))).first();
    }

    /**
     * Adds a new Comment to the collection. The equivalent instruction in the
     * mongo shell would be:
     *
     * <p>
     * db.comments.insertOne({comment})
     *
     * <p>
     *
     * @param comment - Comment object.
     * @throw IncorrectDaoOperation if the insert fails, otherwise returns the
     * resulting Comment object.
     */
    public Comment addComment(Comment comment) throws IncorrectDaoOperation {

        // TODO> Ticket - Update User reviews: implement the functionality that enables adding a new
        // comment.
        // TODO> Ticket - Handling Errors: Implement a try catch block to
        // handle a potential write exception when given a wrong commentId.
        if (comment.getId() == null) {
            throw new IncorrectDaoOperation("", new Throwable());
        } else {
            commentCollection.insertOne(comment);
        }
        return comment;
    }

    /**
     * Updates the comment text matching commentId and user email. This method
     * would be equivalent to running the following mongo shell command:
     *
     * <p>
     * db.comments.update({_id: commentId}, {$set: { "text": text, date:
     * ISODate() }})
     *
     * <p>
     *
     * @param commentId - comment id string value.
     * @param text - comment text to be updated.
     * @param email - user email.
     * @return true if successfully updates the comment text.
     */
    public boolean updateComment(String commentId, String text, String email) {

        // TODO> Ticket - Update User reviews: implement the functionality that enables updating an
        // user own comments
        // TODO> Ticket - Handling Errors: Implement a try catch block to
        // handle a potential write exception when given a wrong commentId.
        Bson filter = Filters.and(Filters.eq("_id", new ObjectId(commentId)),
                Filters.eq("email", email));
        Bson update = Updates.set("text", text);

        Comment comment = commentCollection.findOneAndUpdate(filter, update);

        return comment != null;
    }

    /**
     * Deletes comment that matches user email and commentId.
     *
     * @param commentId - commentId string value.
     * @param email - user email value.
     * @return true if successful deletes the comment.
     */
    public boolean deleteComment(String commentId, String email) throws IncorrectDaoOperation {
        // TODO> Ticket Delete Comments - Implement the method that enables the deletion of a user
        // comment
        // TIP: make sure to match only users that own the given commentId
        // TODO> Ticket Handling Errors - Implement a try catch block to
        // handle a potential write exception when given a wrong commentId.

        if (commentId.length() == 0) {
            throw new IllegalArgumentException("");
        }
        if (email.length() == 0) {
            return false;
        }

        Bson filter = Filters.eq("_id", new ObjectId(commentId));
        Comment comment = commentCollection.find(filter).first();

        if (comment != null && comment.getEmail().equals(email)) {
            commentCollection.deleteOne(filter);
            return true;
        }
        return false;
    }

    /**
     * Ticket: User Report - produce a list of users that comment the most in
     * the website. Query the `comments` collection and group the users by
     * number of comments. The list is limited to up most 20 commenter.
     *
     * @return List {@link Critic} objects.
     */
    public List<Critic> mostActiveCommenters() {
        List<Critic> mostActive = new ArrayList<>();
        // // TODO> Ticket: User Report - execute a command that returns the
        // // list of 20 users, group by number of comments. Don't forget,
        // // this report is expected to be produced with an high durability
        // // guarantee for the returned documents. Once a commenter is in the
        // // top 20 of users, they become a Critic, so mostActive is composed of
        // // Critic objects.
        List<Bson> pipeline = new ArrayList<>();
        
        pipeline.addAll(Arrays.asList(group("$email", sum("count", 1L)),
                sort(descending("count")),
                limit(20)));
        
        MongoCollection<Critic> criticCollection = db.getCollection(COMMENT_COLLECTION, Critic.class)
                .withCodecRegistry(pojoCodecRegistry).withReadConcern(ReadConcern.MAJORITY);
    
        for (Critic critic : criticCollection.aggregate(pipeline)) {
            mostActive.add(critic);
        }
        
        return mostActive;
    }
}
