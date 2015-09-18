/*** BEGIN META {
  "name" : "Export role-strategy permissions to CSV",
  "comment" : "Export the permissions defined in the <a href='https://wiki.jenkins-ci.org/display/JENKINS/Role+Strategy+Plugin'>role-strategy plugin</a> in a CSV format. <br />Further information in the ticket <a href='https://issues.jenkins-ci.org/browse/JENKINS-8075'>JENKINS-8075</a>",
  "parameters" : [],
  "core": "1.424.2",
  "authors" : [
    { name : "Daniel PETISME <danielpetisme> <daniel.petisme@gmail.com>" }
  ]
} END META**/
import hudson.model.Hudson
import com.michelin.cio.hudson.plugins.rolestrategy.RoleBasedAuthorizationStrategy
    
def cleanUsers = { it.flatten().sort().unique() - "null"}

/*
 * UI part
 * The matrix is composed by the match between the roles (columns) and the users (rows).
 * Basically, you can start with this data structure to generate an export in the format you want.
 */
 def export = {matrix, formatter -> formatter(matrix)}
 
 /**
  * The default CSV formatter
  */    
 def csv = { matrix -> matrix.collect{ key, value -> "\n$key, ${value.join(",").replace("true", "x").replace("false", " ")}" } + "\n" }  

/*
 * The script only work with the role-strategy plugin
 * https://wiki.jenkins-ci.org/display/JENKINS/Role+Strategy+Plugin 
 */    
def authStrategy = Hudson.instance.getAuthorizationStrategy()

if(authStrategy instanceof RoleBasedAuthorizationStrategy){
           
   /*
    * Get a [role]:[users] map
    */     
   def permissions = authStrategy.roleMaps.inject([:]){map, it -> map + it.value.grantedRoles}
   
   /*
    * Get all the users defined in the role-strategy plugin
    */    
   def users = cleanUsers(permissions*.value)

   /*
    * Get a [user]:[roles] map
    */    
   def permissionsByUser = users.inject([:]){ map, user ->
      map[user] =  permissions.findAll{ it.value.contains(user)}.collect{it.key.name}
      map
   }

   /*
    *  The matrix building
    */    
   def usersPermissionsMatrix =[:]
  
   /*
    * Get all the roles defined in the role-strategy plugin
    */       
   def roles = authStrategy.getRoleMap(authStrategy.GLOBAL).grantedRoles*.key.name.sort() + authStrategy.getRoleMap(authStrategy.PROJECT).grantedRoles*.key.name.sort()
      
   usersPermissionsMatrix["roles"] = roles
   
   /*
    * Algo:
    * For each user
    *    For each role
    *     matrix[user][role] = hasPermission(user, role)
    *    Done
    * Done                   
    */         
   users.each{ user ->       
      usersPermissionsMatrix[user] = roles.inject([]){ list, role ->
         list << permissionsByUser[user].contains(role)
      }       
   }
   
   /*
    * We're done!! it's time to export the work.
    */        
   println export(usersPermissionsMatrix, csv)

}else{
   println "Not able to list the permissions by user"
} 
