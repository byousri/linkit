package controllers;

import play.*;
import play.jobs.*;
import play.test.*;
import models.*;

@OnApplicationStart
public class PopulateOnStart extends Job {

    public void doJob() {
        // Check if the database is empty
        if(Member.count() == 0) {
            Fixtures.load("init-data.yml");
        }
    }
}