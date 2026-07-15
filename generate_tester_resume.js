const { Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell,
        Header, Footer, AlignmentType, LevelFormat, HeadingLevel, BorderStyle, 
        WidthType, ShadingType, PageNumber } = require('docx');
const fs = require('fs');

// 创建文档
const doc = new Document({
  styles: {
    default: { document: { run: { font: "Microsoft YaHei", size: 22 } } },
    paragraphStyles: [
      { id: "Heading1", name: "Heading 1", basedOn: "Normal", next: "Normal", quickFormat: true,
        run: { size: 28, bold: true, font: "Microsoft YaHei", color: "2E75B6" },
        paragraph: { spacing: { before: 240, after: 120 }, outlineLevel: 0 } },
      { id: "Heading2", name: "Heading 2", basedOn: "Normal", next: "Normal", quickFormat: true,
        run: { size: 24, bold: true, font: "Microsoft YaHei", color: "2E75B6" },
        paragraph: { spacing: { before: 180, after: 80 }, outlineLevel: 1 } },
      { id: "Normal", name: "Normal",
        run: { font: "Microsoft YaHei", size: 22 },
        paragraph: { spacing: { after: 60, line: 360, lineRule: "auto" } } }
    ]
  },
  numbering: {
    config: [
      { reference: "bullets-skill",
        levels: [{ level: 0, format: LevelFormat.BULLET, text: "•", alignment: AlignmentType.LEFT,
          style: { paragraph: { indent: { left: 420, hanging: 280 } } } }] },
      { reference: "bullets-project",
        levels: [{ level: 0, format: LevelFormat.BULLET, text: "•", alignment: AlignmentType.LEFT,
          style: { paragraph: { indent: { left: 420, hanging: 280 } } } }] },
      { reference: "numbers-project",
        levels: [{ level: 0, format: LevelFormat.DECIMAL, text: "%1.", alignment: AlignmentType.LEFT,
          style: { paragraph: { indent: { left: 420, hanging: 280 } } } }] }
    ]
  },
  sections: [{
    properties: {
      page: {
        margin: { top: 720, right: 720, bottom: 720, left: 720 }, // 0.5 inch margins
        size: { width: 11906, height: 16838 } // A4
      }
    },
    headers: {
      default: new Header({ children: [new Paragraph({ 
        children: [new TextRun({ text: "Software Test Engineer Resume", color: "888888", size: 18, font: "Arial" })],
        alignment: AlignmentType.RIGHT
      })] })
    },
    footers: {
      default: new Footer({ children: [new Paragraph({
        alignment: AlignmentType.CENTER,
        children: [new TextRun({ text: "- ", size: 18 }), new TextRun({ children: [PageNumber.CURRENT], size: 18 }), new TextRun({ text: " -", size: 18 })]
      })] })
    },
    children: [
      // ========== 姓名 + 标题 ==========
      new Paragraph({
        alignment: AlignmentType.CENTER,
        spacing: { after: 60 },
        children: [new TextRun({ text: "[你的姓名]", bold: true, size: 44, font: "Microsoft YaHei" })]
      }),
      new Paragraph({
        alignment: AlignmentType.CENTER,
        spacing: { after: 200 },
        children: [new TextRun({ text: "软件测试工程师 / 自动化测试工程师", size: 24, color: "666666" })]
      }),

      // ========== 个人信息表格 ==========
      new Table({
        columnWidths: [2384, 2384, 2384, 2384],
        rows: [new TableRow({
          children: [
            createInfoCell("📧 email@example.com"),
            createInfoCell("📱 138-xxxx-xxxx"),
            createInfoCell("📍 [所在城市]"),
            createInfoCell("💻 github.com/yourname")
          ]
        })]
      }),

      new Paragraph({ spacing: { after: 200 }, children: [] }), // 空行

      // ========== 教育背景 ==========
      createSectionHeader("教育背景"),
      new Paragraph({
        spacing: { after: 100 },
        children: [
          new TextRun({ text: "[大学名称]", bold: true, size: 22 }),
          new TextRun({ text: "\t\t[时间范围，如：2020.09 - 2024.06]", color: "666666", size: 20 })
        ]
      }),
      new Paragraph({
        spacing: { after: 160 },
        children: [
          new TextRun({ text: "[专业名称] | 本科/硕士", size: 22 }),
          new TextRun({ text: "\t", size: 22 }),
          new TextRun({ text: "[GPA：x.x/4.0，如优秀可填写]", color: "666666", size: 20 })
        ]
      }),

      // ========== 专业技能 ==========
      createSectionHeader("专业技能"),
      
      // 技能分类
      createSkillParagraph("自动化测试框架", "JUnit 5 (单元/集成测试)、Puppeteer (前端E2E测试)、MockMvc (API接口测试)"),
      createSkillParagraph("接口测试技术", "RESTful API 测试、HTTP 状态码断言(200/201/401/403/500)、JSON Path 验证、Session/Cookie 管理"),
      createSkillParagraph("数据库测试", "H2 内存数据库配置与隔离、MySQL 兼容性测试、SQL 数据验证、种子数据设计"),
      createSkillParagraph("编程语言", "Java (Spring Boot Test)、JavaScript (Node.js/Puppeteer)、Python (基础)"),
      createSkillParagraph("测试工具链", "Maven 构建集成、Git 版本控制、Node.js 脚本化执行、Postman 接口调试"),
      createSkillParagraph("测试方法论", "等价类划分法、边界值分析法、状态迁移测试、异常场景覆盖、冒烟测试/回归测试"),
      createSkillParagraph("测试报告与文档", "HTML 测试报告自动生成、截图归档管理、TEST_GUIDE 测试指南编写"),

      // ========== 项目经验 ==========
      createSectionHeader("项目经验"),

      // 项目标题
      new Paragraph({
        spacing: { before: 120, after: 80 },
        children: [
          new TextRun({ text: "图书馆座位预约管理系统 — 自动化测试体系建设", bold: true, size: 24, color: "2E75B6" })
        ]
      }),
      new Paragraph({
        spacing: { after: 80 },
        children: [
          new TextRun({ text: "项目描述：", bold: true, size: 21 }),
          new TextRun({ text: "基于 Spring Boot + Vue 的图书馆座位预约系统，支持用户注册登录、图书馆查询、座位浏览与预约、签到签退等核心功能。", size: 21 })
        ]
      }),
      new Paragraph({
        spacing: { after: 80 },
        children: [
          new TextRun({ text: "测试职责：", bold: true, size: 21 }),
          new TextRun({ text: "独立负责项目全栈自动化测试体系搭建，包括后端 API 测试和前端 E2E 测试。", size: 21 })
        ]
      }),
      new Paragraph({
        spacing: { after: 80 },
        children: [
          new TextRun({ text: "技术栈：", bold: true, size: 21 }),
          new TextRun({ text: "JUnit 5 | Spring Boot Test | MockMvc | H2 Database | Puppeteer | Node.js | HTML Report", size: 21 })
        ]
      }),

      // 后端 API 测试详情
      new Paragraph({
        numbering: { reference: "bullets-project", level: 0 },
        spacing: { before: 80, after: 40 },
        children: [
          new TextRun({ text: "后端 API 自动化测试", bold: true, size: 21 })
        ]
      }),
      new Paragraph({
        indent: { left: 700 },
        spacing: { after: 30 },
        children: [new TextRun({ text: "基于 JUnit 5 + MockMvc 构建 HTTP 层接口测试框架，模拟完整请求-响应生命周期", size: 21 })]
      }),
      new Paragraph({
        indent: { left: 700 },
        spacing: { after: 30 },
        children: [new TextRun({ text: "采用 H2 内存数据库实现测试环境隔离，确保每次测试独立运行且无外部依赖", size: 21 })]
      }),
      new Paragraph({
        indent: { left: 700 },
        spacing: { after: 30 },
        children: [new TextRun({ text: "认证模块：覆盖登录成功/失败、注册验证、登出流程、未授权访问拦截(401)", size: 21 })]
      }),
      new Paragraph({
        indent: { left: 700 },
        spacing: { after: 30 },
        children: [new TextRun({ text: "预约模块：覆盖创建预约、查询列表、取消操作、管理员权限校验(403)", size: 21 })]
      }),
      new Paragraph({
        indent: { left: 700 },
        spacing: { after: 30 },
        children: [new TextRun({ text: "数据模块：图书馆 CRUD 操作、座位查询、分页功能验证与数据一致性检查", size: 21 })]
      }),
      new Paragraph({
        indent: { left: 700 },
        spacing: { after: 60 },
        children: [new TextRun({ text: "实现 Session 状态管理、JSON Path 深度断言、异常场景边界值测试", size: 21 })]
      }),

      // 前端 E2E 测试详情
      new Paragraph({
        numbering: { reference: "bullets-project", level: 0 },
        spacing: { before: 80, after: 40 },
        children: [
          new TextRun({ text: "前端 E2E 自动化测试", bold: true, size: 21 })
        ]
      }),
      new Paragraph({
        indent: { left: 700 },
        spacing: { after: 30 },
        children: [new TextRun({ text: "使用 Puppeteer 模拟移动端真实用户操作（375×812 viewport），还原生产环境体验", size: 21 })]
      }),
      new Paragraph({
        indent: { left: 700 },
        spacing: { after: 30 },
        children: [new TextRun({ text: "登录流程验证：正确凭据登录、错误密码提示、空输入校验、页面跳转逻辑", size: 21 })]
      }),
      new Paragraph({
        indent: { left: 700 },
        spacing: { after: 30 },
        children: [new TextRun({ text: "注册流程验证：表单完整性填写、重复账号检测机制、注册成功自动跳转", size: 21 })]
      }),
      new Paragraph({
        indent: { left: 700 },
        spacing: { after: 30 },
        children: [new TextRun({ text: "预约核心业务闭环测试：选座→预约→确认→查看，完整端到端流程覆盖", size: 21 })]
      }),
      new Paragraph({
        indent: { left: 700 },
        spacing: { after: 60 },
        children: [new TextRun({ text: "我的预约页面交互验证：状态实时显示、筛选功能、取消操作反馈机制", size: 21 })]
      }),

      // 测试工程化实践
      new Paragraph({
        numbering: { reference: "bullets-project", level: 0 },
        spacing: { before: 80, after: 40 },
        children: [
          new TextRun({ text: "测试工程化与质量保障体系", bold: true, size: 21 })
        ]
      }),
      new Paragraph({
        indent: { left: 700 },
        spacing: { after: 30 },
        children: [new TextRun({ text: "编写 TEST_GUIDE.md 完整测试文档（267行），涵盖环境配置、运行命令、用例设计说明", size: 21 })]
      }),
      new Paragraph({
        indent: { left: 700 },
        spacing: { after: 30 },
        children: [new TextRun({ text: "设计 data.sql 测试种子数据集，支持 MD5 密码加密，保障测试数据一致性与可复现性", size: 21 })]
      }),
      new Paragraph({
        indent: { left: 700 },
        spacing: { after: 30 },
        children: [new TextRun({ text: "封装公共辅助函数库（login/screenshot/sleep/wait），提升测试代码复用率与维护效率", size: 21 })]
      }),
      new Paragraph({
        indent: { left: 700 },
        spacing: { after: 30 },
        children: [new TextRun({ text: "实现配置化管理方案：URL、用户凭据、超时参数均通过 config.js 统一配置调整", size: 21 })]
      }),
      new Paragraph({
        indent: { left: 700 },
        spacing: { after: 30 },
        children: [new TextRun({ text: "构建 HTML 可视化测试报告系统：自动统计通过率、关键节点截图归档管理", size: 21 })]
      }),
      new Paragraph({
        indent: { left: 700 },
        spacing: { after: 100 },
        children: [new TextRun({ text: "采用模块化架构设计（config/helpers/tests 分层解耦），便于团队协作与长期维护", size: 21 })]
      }),

      // ========== 自我评价 ==========
      createSectionHeader("自我评价"),
      new Paragraph({
        spacing: { after: 60 },
        children: [new TextRun({ 
          text: "具备扎实的软件测试理论基础和自动化测试实战经验。熟悉 Java 后端接口测试和前端 E2E 测试全流程，能够独立搭建从零到一的自动化测试体系。注重测试用例设计的完整性、可维护性和可复现性，善于发现潜在的业务逻辑缺陷和边界条件问题。有良好的技术文档编写习惯，能够输出规范的测试计划、测试报告和团队协作指南。对软件质量保障工作有高度责任心，持续关注业界测试新技术和最佳实践。", 
          size: 21 
        })]
      }),

      // ========== 致谢语 ==========
      new Paragraph({ spacing: { before: 300 }, children: [] }),
      new Paragraph({
        alignment: AlignmentType.RIGHT,
        children: [new TextRun({ text: "期待有机会与您面谈，感谢您的时间！", italics: true, size: 20, color: "666666" })]
      }),
    ]
  }]
});

// ========== 辅助函数 ==========

// 创建信息单元格
function createInfoCell(text) {
  const border = { style: BorderStyle.NIL };
  return new TableCell({
    borders: { top: border, bottom: border, left: border, right: border },
    width: { size: 2384, type: WidthType.DXA },
    shading: { fill: "F5F9FC", type: ShadingType.CLEAR },
    margins: { top: 60, bottom: 60, left: 100, right: 100 },
    children: [new Paragraph({ 
      alignment: AlignmentType.CENTER,
      children: [new TextRun({ text, size: 19, color: "555555" })] 
    })]
  });
}

// 创建章节标题
function createSectionHeader(title) {
  return new Paragraph({
    heading: HeadingLevel.HEADING_1,
    spacing: { before: 240, after: 120 },
    border: { bottom: { style: BorderStyle.SINGLE, size: 12, color: "2E75B6", space: 1 } },
    children: [new TextRun({ text: title, bold: true, size: 26, color: "2E75B6" })]
  });
}

// 创建技能段落（标签 + 内容）
function createSkillParagraph(label, content) {
  return new Paragraph({
    numbering: { reference: "bullets-skill", level: 0 },
    spacing: { after: 50 },
    children: [
      new TextRun({ text: label + "：", bold: true, size: 21, color: "333333" }),
      new TextRun({ text: content, size: 21, color: "444444" })
    ]
  });
}

// 生成文档
async function main() {
  try {
    const buffer = await Packer.toBuffer(doc);
    const outputPath = 'c:/Users/yuan/Desktop/政治理论/library_reservation_g-master/Software_Test_Engineer_Resume_优化版.docx';
    fs.writeFileSync(outputPath, buffer);
    console.log('✅ 简历生成成功！');
    console.log('📄 文件路径: ' + outputPath);
  } catch (err) {
    console.error('❌ 生成失败:', err.message);
    process.exit(1);
  }
}

main();
