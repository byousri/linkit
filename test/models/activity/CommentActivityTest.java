package models.activity;

import models.Comment;
import models.Member;
import models.Session;
import org.junit.*;

/**
 * Unit tests for {@link CommentActivity} domain object
 * @author Sryl <cyril.lacote@gmail.com>
 */
public class CommentActivityTest extends AbstractActivityTest {

    @Test
    public void addComment() {
        Session s = Session.all().first();
        
        // Non activity for the session
        assertEquals(0, Activity.count("session = ?", s));
        
        Comment c = new Comment(member, s, "Un commentaire");
        s.addComment(c);
        s.save();
        
        // One activity for the session
        assertEquals(1, Activity.count("session = ?", s));
        Activity a = Activity.find("session = ?", s).first();
        assertActivity(a);
        assertTrue(a instanceof CommentActivity);
        CommentActivity ca = (CommentActivity) a;
        assertEquals(member, ca.member);
        assertEquals(s, ca.session);
        assertEquals(c, ca.comment);
    }
}
