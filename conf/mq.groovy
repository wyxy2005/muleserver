NONE="none"
Object res = muleContext.client.request("jms://"+hostName+"?connector="+connectorRef, 1000);
println(res)
if ( res == null){
    println("no resonse");
    return NONE
}else{
    return res
}