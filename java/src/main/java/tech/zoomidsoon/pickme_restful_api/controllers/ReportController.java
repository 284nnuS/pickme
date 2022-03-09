package tech.zoomidsoon.pickme_restful_api.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse;
import tech.zoomidsoon.pickme_restful_api.helpers.Result;
import tech.zoomidsoon.pickme_restful_api.models.Report;
import tech.zoomidsoon.pickme_restful_api.helpers.SQLErrors;
import tech.zoomidsoon.pickme_restful_api.repos.ReportRepository;
import tech.zoomidsoon.pickme_restful_api.utils.DBContext;

@Path("/report")
public class ReportController {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAllReport() {
        try {
            try (Connection conn = DBContext.getConnection()) {
                List<Report> reports = ReportRepository.getInstance().readAll(conn);

                return JsonAPIResponse.ok(reports);
            } catch (SQLException e) {
                Response response = JsonAPIResponse.handleSQLError(e);
                if (response != null)
                    return response;
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }

        return JsonAPIResponse.handleError(JsonAPIResponse.SERVER_ERROR);
    }

    @Path("/get")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMessages(ReportRepository.FindByTimeAndReportedId findByTimeAndUserId) {
        try {
            try (Connection conn = DBContext.getConnection()) {
                List<Report> reports = ReportRepository.getInstance().read(conn, findByTimeAndUserId);
                return JsonAPIResponse.ok(reports);
            } catch (SQLException e) {
                Response response = JsonAPIResponse.handleSQLError(e);
                if (response != null)
                    return response;
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }

        return JsonAPIResponse.handleError(JsonAPIResponse.SERVER_ERROR);
    }

    @GET
    @Path("/id/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") Long reportId) {
        try {
            try (Connection conn = DBContext.getConnection()) {
                ReportRepository.FindById findById = new ReportRepository.FindById(reportId);
                List<Report> reports = ReportRepository.getInstance().read(conn, findById);

                if (reports.isEmpty())
                    return JsonAPIResponse.handleError(404, "Report does not exist", "");
                return JsonAPIResponse.ok(reports.get(0));
            } catch (SQLException e) {
                Response response = JsonAPIResponse.handleSQLError(e);
                if (response != null)
                    return response;
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }

        return JsonAPIResponse.handleError(JsonAPIResponse.SERVER_ERROR);
    }

    @GET
    @Path("/status/{done}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("done") Boolean done) {
        try {
            try (Connection conn = DBContext.getConnection()) {
                ReportRepository.FindByDone findByDone = new ReportRepository.FindByDone(done);
                List<Report> reports = ReportRepository.getInstance().read(conn, findByDone);

                if (reports.isEmpty())
                    return JsonAPIResponse.handleError(404, "Report does not exist", "");
                return JsonAPIResponse.ok(reports);
            } catch (SQLException e) {
                Response response = JsonAPIResponse.handleSQLError(e);
                if (response != null)
                    return response;
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }

        return JsonAPIResponse.handleError(JsonAPIResponse.SERVER_ERROR);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewReport(Report report) {
        try {
            try (Connection conn = DBContext.getConnection()) {
                Result<Report, JsonAPIResponse.Error> result = ReportRepository.getInstance().create(conn, report);

                return JsonAPIResponse.handleResult(result);
            }
        } catch (SQLException e) {
            Response response = JsonAPIResponse.handleSQLError(e,
                    SQLErrors.DATA_TRUNCATED,
                    SQLErrors.INCORRECT_DATA_TYPE,
                    SQLErrors.CHECK_CONSTANT);
            if (response != null)
                return response;
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return JsonAPIResponse.handleError(JsonAPIResponse.SERVER_ERROR);
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateReport(Report report) {
        try {
            try (Connection conn = DBContext.getConnection()) {
                Result<Report, JsonAPIResponse.Error> result = ReportRepository.getInstance().update(conn, report);
                return JsonAPIResponse.handleResult(result);
            } catch (SQLException e) {
                Response response = JsonAPIResponse.handleSQLError(e);
                if (response != null)
                    return response;
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }

        return JsonAPIResponse.handleError(JsonAPIResponse.SERVER_ERROR);
    }
}
