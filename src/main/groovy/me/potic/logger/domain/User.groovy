package me.potic.logger.domain

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString(includes = [ 'id' ])
@EqualsAndHashCode
class User {

    String id

    Collection<String> socialIds

    String pocketAccessToken
}
