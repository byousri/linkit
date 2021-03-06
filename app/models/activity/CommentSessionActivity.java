package models.activity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import models.SessionComment;
import models.Member;
import models.Session;
import play.data.validation.Required;
import play.i18n.Messages;
import play.mvc.Router;

/**
 * A comment activity : someone ({@link Activity#member} commented on a session ({@link Activity#session}
 * @author Agnes <agnes.crepet@gmail.com>
 * @author Sryl <cyril.lacote@gmail.com>
 */
@Entity
public class CommentSessionActivity extends CommentActivity {

    @Required
    @ManyToOne
    public SessionComment comment;

    public CommentSessionActivity(Member author, Session session, SessionComment comment) {
        super(author);
        this.session = session;
        this.comment = comment;
    }

    @Override
    public String getMessage(String lang) {
        return Messages.get(getMessageKey(), member, session, comment);
    }

    @Override
    public String getUrl() {
        return Router
                .reverse("Sessions.show")
                .add("sessionId", session.id)
                .addRef("comment"+comment.id)
                .url;
    }
}
