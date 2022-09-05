# springIOC
* 实现一个IOC容器
 * 1:根据需求编写XML配置文件，配置需要创建的bean
 * 2:编写程序读取XML文件，获取bean相关信息，类属性，id
 * 3:根据第二步获取的信息，结合反射动态创建对象，同时完成属性的赋值
 * 4：将创建好的bean存入Map集合。设置key-value映射，key就是bean的id，value就是bean对象
 * 5:提供方法从Map中通过id获取对应的value
