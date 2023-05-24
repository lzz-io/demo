package ${mapperInterface.packageName};

import ${tableClass.fullClassName};

import java.util.List;

/**
 * @author ${author!}
 * @description 针对表【${tableClass.tableName}<#if tableClass.remark?has_content>(${tableClass.remark!})</#if>】的数据库操作Mapper
 * @createDate ${.now?string('yyyy-MM-dd HH:mm:ss')}
 * @Entity ${tableClass.fullClassName}
 */
public interface ${mapperInterface.fileName} {

    List<${tableClass.shortClassName}> selectAll();

    ${tableClass.shortClassName} selectByPrimaryKey(<#if (tableClass.pkFields?size==1)>${tableClass.pkFields[0].shortTypeName}</#if> id);

    int deleteByPrimaryKey(<#if (tableClass.pkFields?size==1)>${tableClass.pkFields[0].shortTypeName}</#if> id);

    int insert(${tableClass.shortClassName} record);

    int insertSelective(${tableClass.shortClassName} record);

    int updateByPrimaryKeySelective(${tableClass.shortClassName} record);

    int updateByPrimaryKey(${tableClass.shortClassName} record);

}
