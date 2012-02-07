import hudson.FilePath;

for (slave in hudson.model.Hudson.instance.slaves)
{ 
  FilePath fp = slave.createPath(slave.getRootPath().toString() + File.separator + "workspace"); 
  fp.deleteRecursive(); 
}